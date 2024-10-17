package cn.lary.module.wallet.service.impl;

import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.dto.BatchOutcomeTransferDTO;
import cn.lary.module.wallet.dto.SystemIncomeTransferDTO;
import cn.lary.module.wallet.dto.SystemOutcomeTransferDTO;
import cn.lary.module.wallet.dto.TransferDTO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.entity.WalletIncome;
import cn.lary.module.wallet.entity.WalletOutcome;
import cn.lary.module.wallet.mapper.WalletMapper;
import cn.lary.module.wallet.service.WalletIncomeService;
import cn.lary.module.wallet.service.WalletOutcomeService;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-09-13
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    private final WalletIncomeService walletIncomeService;
    private final WalletOutcomeService walletOutcomeService;
    private final UserService userService;
    private final TransactionTemplate transactionTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponsePair<Void> transfer(TransferDTO dto) {
        Wallet uidWallet = lambdaQuery()
                .select(Wallet::getVcFee)
                .select(Wallet::getVcLocked)
                .eq(Wallet::getUid, dto.getUid())
                .eq(Wallet::getIsBlock, false)
                .one();
        if (uidWallet == null) {
            return BizKit.fail("uid wallet not exist");
        }
        Wallet toUidWallet = lambdaQuery()
                .select(Wallet::getVcLocked)
                .select(Wallet::getVcFee)
                .eq(Wallet::getUid, dto.getToUid())
                .eq(Wallet::getIsBlock, false)
                .one();
        if (toUidWallet == null) {
            return BizKit.fail("to uid wallet not exist");
        }
        if (uidWallet.getVcFee() - uidWallet.getVcLocked() < dto.getAmount()) {
            return BizKit.fail("uid balance not enough");
        }
        //update
        lambdaUpdate()
                .eq(Wallet::getUid, dto.getUid())
                .setIncrBy(Wallet::getVcOutcome, dto.getAmount())
                .setDecrBy(Wallet::getVcFee, dto.getAmount());
        lambdaUpdate()
                .eq(Wallet::getUid, dto.getToUid())
                .setIncrBy(Wallet::getVcFee,dto.getAmount())
                .setIncrBy(Wallet::getVcIncome,dto.getAmount());
        walletIncomeService.save(WalletIncome.of(dto));
        walletOutcomeService.save(WalletOutcome.of(dto));
        return BizKit.ok();
    }

    @Override
    public ResponsePair<Void> batchOutcomeTransfer(BatchOutcomeTransferDTO dto) {
        return transactionTemplate.execute((status)->{
            if (dto == null) {
                return BizKit.fail("dto is null");
            }
            if (CollectionKit.isEmpty(dto.getRecipients())) {
                return BizKit.fail("recipients is empty");
            }
            if (dto.getAmount() == 0 || dto.getTotalAmount() == 0) {
                return BizKit.fail("amount not match");
            }
            if (dto.getTotalAmount() != dto.getAmount() * dto.getRecipients().size()) {
                return BizKit.fail("total amount error");
            }
            Wallet uidWallet = lambdaQuery()
                    .select(Wallet::getVcFee)
                    .eq(Wallet::getUid, dto.getUid())
                    .eq(Wallet::getIsBlock, false)
                    .one();
            if (uidWallet == null) {
                return BizKit.fail("uid wallet not exist");
            }
            // outcome
            List<WalletIncome> walletIncomes = new ArrayList<>();
            List<WalletOutcome> walletOutcomes = new ArrayList<>();

            dto.getRecipients().forEach(uid->{
                WalletOutcome out = new WalletOutcome()
                        .setUid(dto.getUid())
                        .setToUid(uid)
                        .setCost(dto.getAmount())
                        .setType(dto.getType())
                        .setChannelId(dto.getChannelId())
                        .setChannelType(dto.getChannelType());
                walletOutcomes.add(out);
                WalletIncome in = new WalletIncome()
                        .setUid(uid)
                        .setToUid(dto.getUid())
                        .setCost(dto.getAmount())
                        .setType(dto.getType())
                        .setChannelId(dto.getChannelId())
                        .setChannelType(dto.getChannelType());
                walletIncomes.add(in);
            });
            walletOutcomeService.saveBatch(walletOutcomes);
            walletIncomeService.saveBatch(walletIncomes);
            lambdaUpdate()
                    .setDecrBy(Wallet::getVcFee, dto.getTotalAmount())
                    .eq(Wallet::getUid,dto.getUid());
            // tip,mp setIncrBy() method  since 3.5.6
            lambdaUpdate()
                    .setIncrBy(Wallet::getVcFee,dto.getAmount())
                    .in(Wallet::getUid,dto.getRecipients());
            return BizKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> systemOutcomeTransfer(SystemOutcomeTransferDTO dto) {
        return transactionTemplate.execute(status -> {
            ResponsePair<List<Wallet>> data = getUserWallets(dto.getMembers());
            if(data.isFail()) {
                return BizKit.fail(data.getMsg());
            }
            List<Wallet> userWallets = data.getData();
            List<WalletIncome> walletIncomes = new ArrayList<>();
            userWallets.forEach(user->{
                WalletIncome out = new WalletIncome()
                        .setUid(user.getUid())
                        .setCost(dto.getAmount())
                        .setType(dto.getType());
                walletIncomes.add(out);
            });
            List<Integer> users = userWallets
                    .stream()
                    .map(Wallet::getUid)
                    .toList();
            walletIncomeService.saveBatch(walletIncomes);
            lambdaUpdate()
                    .setIncrBy(Wallet::getVcFee, dto.getAmount())
                    .in(Wallet::getUid,users);
            return BizKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> systemIncomeTransfer(SystemIncomeTransferDTO dto) {
        return transactionTemplate.execute(status -> {
            ResponsePair<List<Wallet>> data = getUserWallets(dto.getMembers());
            if(data.isFail()) {
                return BizKit.fail(data.getMsg());
            }
            List<Wallet> userWallets = data.getData();
            List<Integer> validUsers = userWallets
                    .stream()
                    .filter(u -> u.getVcFee() - u.getVcLocked() >= dto.getAmount())
                    .map(Wallet::getUid)
                    .toList();
            if(CollectionKit.isEmpty(validUsers)) {
                return BizKit.fail("valid users is empty");
            }
            List<WalletOutcome> walletOutcomes = validUsers
                    .stream()
                    .map(u -> {
                        WalletOutcome out = new WalletOutcome();
                        out.setUid(u);
                        out.setCost(dto.getAmount());
                        out.setType(dto.getType());
                        return out;
            }).toList();
            walletOutcomeService.saveBatch(walletOutcomes);
            lambdaUpdate()
                    .setDecrBy(Wallet::getVcFee, dto.getAmount())
                    .in(Wallet::getUid, validUsers);
            return BizKit.ok();
        });
    }

    @Override
    public ResponsePair<List<Wallet>> getUserWallets(List<Integer> members) {
        if (CollectionKit.isEmpty(members)) {
            return BizKit.fail("members null");
        }
        List<Integer> users = userService.getValidUsers(members);
        if (CollectionKit.isEmpty(users)) {
            return BizKit.fail("users empty");
        }
        List<Wallet> userWallets = lambdaQuery()
                .select(Wallet::getVcFee)
                .select(Wallet::getVcLocked)
                .select(Wallet::getUid)
                .eq(Wallet::getIsBlock, false)
                .in(Wallet::getUid, users)
                .list();
        if (userWallets.isEmpty()) {
            return BizKit.fail("wallet error");
        }
        return BizKit.ok(userWallets);
    }

}
