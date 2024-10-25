package cn.lary.module.wallet.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.dto.*;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.entity.WalletIncome;
import cn.lary.module.wallet.entity.WalletOutcome;
import cn.lary.module.wallet.mapper.WalletMapper;
import cn.lary.module.wallet.service.WalletIncomeService;
import cn.lary.module.wallet.service.WalletOutcomeService;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.module.wallet.vo.BalanceVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    private final WalletIncomeService walletIncomeService;
    private final WalletOutcomeService walletOutcomeService;
    private final UserService userService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponsePair<Void> transfer(TransferDTO dto) {
        return transactionTemplate.execute(status -> {
            Wallet uidWallet = lambdaQuery()
                    .select(Wallet::getVcFee)
                    .select(Wallet::getVcLocked)
                    .eq(Wallet::getUid, dto.getUid())
                    .eq(Wallet::getIsBlock, false)
                    .one();
            if (uidWallet == null) {
                return BusinessKit.fail("uid wallet not exist");
            }
            Wallet toUidWallet = lambdaQuery()
                    .select(Wallet::getVcLocked)
                    .select(Wallet::getVcFee)
                    .eq(Wallet::getUid, dto.getToUid())
                    .eq(Wallet::getIsBlock, false)
                    .one();
            if (toUidWallet == null) {
                return BusinessKit.fail("to uid wallet not exist");
            }
            if (uidWallet.getVcFee() - uidWallet.getVcLocked() < dto.getAmount()) {
                return BusinessKit.fail("uid balance not enough");
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
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> batchOutcomeTransfer(BatchOutcomeTransferDTO dto) {
        return transactionTemplate.execute((status)->{
            if (dto == null) {
                return BusinessKit.fail("dto is null");
            }
            if (CollectionKit.isEmpty(dto.getRecipients())) {
                return BusinessKit.fail("recipients is empty");
            }
            if (dto.getAmount() == 0 || dto.getTotalAmount() == 0) {
                return BusinessKit.fail("amount not match");
            }
            if (dto.getTotalAmount() != dto.getAmount() * dto.getRecipients().size()) {
                return BusinessKit.fail("total amount error");
            }
            Wallet uidWallet = lambdaQuery()
                    .select(Wallet::getVcFee)
                    .eq(Wallet::getUid, dto.getUid())
                    .eq(Wallet::getIsBlock, false)
                    .one();
            if (uidWallet == null) {
                return BusinessKit.fail("uid wallet not exist");
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
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> systemOutcomeTransfer(SystemOutcomeTransferDTO dto) {
        return transactionTemplate.execute(status -> {
            ResponsePair<List<Wallet>> data = getUserWallets(dto.getMembers());
            if(data.isFail()) {
                return BusinessKit.fail(data.getMsg());
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
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> systemIncomeTransfer(SystemIncomeTransferDTO dto) {
        return transactionTemplate.execute(status -> {
            ResponsePair<List<Wallet>> data = getUserWallets(dto.getMembers());
            if(data.isFail()) {
                return BusinessKit.fail(data.getMsg());
            }
            List<Wallet> userWallets = data.getData();
            List<Integer> validUsers = userWallets
                    .stream()
                    .filter(u -> u.getVcFee() - u.getVcLocked() >= dto.getAmount())
                    .map(Wallet::getUid)
                    .toList();
            if(CollectionKit.isEmpty(validUsers)) {
                return BusinessKit.fail("valid users is empty");
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
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<List<Wallet>> getUserWallets(List<Integer> members) {
        if (CollectionKit.isEmpty(members)) {
            return BusinessKit.fail("members null");
        }
        List<Integer> users = userService.getValidUsers(members);
        if (CollectionKit.isEmpty(users)) {
            return BusinessKit.fail("users empty");
        }
        List<Wallet> userWallets = lambdaQuery()
                .select(Wallet::getVcFee)
                .select(Wallet::getVcLocked)
                .select(Wallet::getUid)
                .eq(Wallet::getIsBlock, false)
                .in(Wallet::getUid, users)
                .list();
        if (userWallets.isEmpty()) {
            return BusinessKit.fail("wallet error");
        }
        return BusinessKit.ok(userWallets);
    }

    @Override
    public ResponsePair<Void> updateQuestion(UpdateSecurityQuestionDTO dto) {
        int uid = RequestContext.getLoginUID();
        Wallet wallet = lambdaQuery()
                .select(Wallet::getUid)
                .eq(Wallet::getUid, uid)
                .one();
        if (wallet == null) {
            return  BusinessKit.fail("wallet status error");
        }
        if(StringKit.diff(StringKit.MD5(dto.getBeforeAnswer()),wallet.getSecAnswer())) {
            return BusinessKit.fail("question answer error");
        }
        lambdaUpdate()
                .set(Wallet::getSecAnswer,dto.getAfterQuestion())
                .set(Wallet::getSecQuestion,StringKit.MD5(dto.getAfterAnswer()))
                .eq(Wallet::getUid, uid);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<BalanceVO> my() {
        Wallet wallet = lambdaQuery()
                .select(Wallet::getVcFee, Wallet::getVcLocked)
                .select(Wallet::getVcOutcome, Wallet::getVcIncome)
                .select(Wallet::getIsAnchor, Wallet::getSecQuestion)
                .eq(Wallet::getUid, RequestContext.getLoginUID())
                .one();
        if (wallet == null) {
            log.error("query wallet error,not found,uid :{}",RequestContext.getLoginUID());
            return BusinessKit.fail("wallet not exist");
        }
        return BusinessKit.ok(new BalanceVO(wallet));
    }

}
