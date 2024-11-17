package cn.lary.module.wallet.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.common.handler.LaryMetaObjectHandler;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.dto.*;
import cn.lary.module.wallet.entity.BatchOutcomeRandomTransfer;
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
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
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
    private final TransactionTemplate transactionTemplate;
    private final WalletMapper walletMapper;


    @Override
    public ResponsePair<Void> transfer(TransferDTO dto) {
        return transactionTemplate.execute(status -> {
            Wallet uidWallet = lambdaQuery()
                    .select(Wallet::getAmount)
                    .eq(Wallet::getUid, dto.getUid())
                    .one();
            if (uidWallet == null || uidWallet.getStatus() == LARY.STATUS.BLOCK ) {
                return BusinessKit.fail("uid wallet not exist");
            }
            Wallet toUidWallet = lambdaQuery()
                    .select(Wallet::getAmount)
                    .eq(Wallet::getUid, dto.getToUid())
                    .one();
            if (toUidWallet == null  || toUidWallet.getStatus() == LARY.STATUS.BLOCK) {
                return BusinessKit.fail("to uid wallet not exist");
            }
            if (uidWallet.getAmount().compareTo(dto.getAmount()) < 0) {
                return BusinessKit.fail("uid balance not enough");
            }
            //update
            lambdaUpdate()
                    .eq(Wallet::getUid, dto.getUid())
                    .setIncrBy(Wallet::getOutcome, dto.getAmount())
                    .setDecrBy(Wallet::getPocket, dto.getAmount());
            lambdaUpdate()
                    .eq(Wallet::getUid, dto.getToUid())
                    .setIncrBy(Wallet::getAmount,dto.getAmount())
                    .setIncrBy(Wallet::getIncome,dto.getAmount());
            walletIncomeService.save(WalletIncome.of(dto));
            walletOutcomeService.save(WalletOutcome.of(dto));
            return BusinessKit.ok();
        });
    }


    @Override
    public ResponsePair<Void> batchOutcomeRandomTransfer(BatchOutcomeRandomTransferDTO dto) {
        if (dto == null) {
            return BusinessKit.fail("dto is null");
        }
        if (CollectionKit.isEmpty(dto.getRecipients())) {
            return BusinessKit.fail("recipients is empty");
        }
        if (CollectionKit.isEmpty(dto.getAmount())) {
            return BusinessKit.fail("amount is empty");
        }
        if (dto.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BusinessKit.fail("total amount is error");
        }
        if (dto.getAmount().size() != dto.getRecipients().size()) {
            return BusinessKit.fail("recipients and amount num not match");
        }

        return transactionTemplate.execute(status->{
            Wallet uidWallet = lambdaQuery()
                    .select(Wallet::getAmount)
                    .eq(Wallet::getUid, dto.getUid())
                    .one();
            if (uidWallet == null || uidWallet.getStatus() == LARY.STATUS.BLOCK) {
                return BusinessKit.fail("uid wallet not exist");
            }
            if (uidWallet.getAmount().compareTo(dto.getTotalAmount()) < 0) {
                return BusinessKit.fail("total amount not enough");
            }
            // outcome
            List<WalletIncome> walletIncomes = new ArrayList<>();
            List<WalletOutcome> walletOutcomes = new ArrayList<>();
            List<BatchOutcomeRandomTransfer> transfers = new ArrayList<>();
            for (int i = 0;i < dto.getRecipients().size();i++) {
                WalletOutcome out = new WalletOutcome()
                        .setUid(dto.getUid())
                        .setToUid(dto.getRecipients().get(i))
                        .setAmount(dto.getAmount().get(i))
                        .setType(dto.getType())
                        .setChannel(dto.getChannel())
                        .setCategory(dto.getCategory());
                walletOutcomes.add(out);
                WalletIncome in = new WalletIncome()
                        .setUid(dto.getRecipients().get(i))
                        .setToUid(dto.getUid())
                        .setAmount(dto.getAmount().get(i))
                        .setType(dto.getType())
                        .setChannel(dto.getChannel())
                        .setCategory(dto.getCategory());
                walletIncomes.add(in);
                transfers.add(new BatchOutcomeRandomTransfer()
                        .setUser(dto.getRecipients().get(i))
                        .setAmount(dto.getAmount().get(i)));
            }
            walletOutcomeService.saveBatch(walletOutcomes);
            walletIncomeService.saveBatch(walletIncomes);
            lambdaUpdate()
                    .setDecrBy(Wallet::getAmount, dto.getTotalAmount())
                    .eq(Wallet::getUid,dto.getUid());
            walletMapper.batchOutcomeRandomTransfer(transfers);
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> batchOutcomeFixTransfer(BatchOutcomeFixTransferDTO dto) {
        if (dto == null) {
            return BusinessKit.fail("dto is null");
        }
        if (CollectionKit.isEmpty(dto.getRecipients())) {
            return BusinessKit.fail("recipients is empty");
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) == 0
                || dto.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BusinessKit.fail("amount not match");
        }
        if (dto.getTotalAmount()
                .compareTo( dto.getAmount()
                        .multiply(BigDecimal.valueOf(dto.getRecipients().size()))
                ) != 0) {
            return BusinessKit.fail("total amount error");
        }
        return transactionTemplate.execute((status)->{
            Wallet uidWallet = lambdaQuery()
                    .select(Wallet::getPocket)
                    .eq(Wallet::getUid, dto.getUid())
                    .one();
            if (uidWallet == null || uidWallet.getStatus() == LARY.STATUS.BLOCK) {
                return BusinessKit.fail("uid wallet not exist");
            }
            if (uidWallet.getPocket().compareTo(dto.getTotalAmount()) < 0) {
                return BusinessKit.fail("total amount not enough");
            }
            // outcome
            List<WalletIncome> walletIncomes = new ArrayList<>();
            List<WalletOutcome> walletOutcomes = new ArrayList<>();

            dto.getRecipients().forEach(uid->{
                WalletOutcome out = new WalletOutcome()
                        .setUid(dto.getUid())
                        .setToUid(uid)
                        .setAmount(dto.getAmount())
                        .setType(dto.getType())
                        .setChannel(dto.getChannel())
                        .setCategory(dto.getCategory());
                walletOutcomes.add(out);
                WalletIncome in = new WalletIncome()
                        .setUid(uid)
                        .setToUid(dto.getUid())
                        .setAmount(dto.getAmount())
                        .setType(dto.getType())
                        .setChannel(dto.getChannel())
                        .setCategory(dto.getCategory());
                walletIncomes.add(in);
            });
            walletOutcomeService.saveBatch(walletOutcomes);
            walletIncomeService.saveBatch(walletIncomes);

            decr(dto.getUid(),dto.getAmount(),dto.getTransfer());
            incr(dto.getRecipients(),dto.getAmount(),dto.getTransfer());
            // tip,mp setIncrBy() method  since 3.5.6
            return BusinessKit.ok();
        });
    }
    private void decr(long uid,BigDecimal amount,int transfer) {
       switch (transfer) {
           case LARY.WALLET.TRANSFER.AMOUNT -> {
               lambdaUpdate()
                       .setDecrBy(Wallet::getAmount, amount)
                       .setIncrBy(Wallet::getOutcome,amount)
                       .eq(Wallet::getUid,uid);

           }
           case LARY.WALLET.TRANSFER.POCKET -> {
               lambdaUpdate()
                       .setDecrBy(Wallet::getPocket, amount)
                       .setIncrBy(Wallet::getOutcome,amount)
                       .eq(Wallet::getUid,uid);
           }
       }
    }
    private void incr(long uid,BigDecimal amount,int transfer) {
        switch (transfer) {
            case LARY.WALLET.TRANSFER.AMOUNT -> {
                lambdaUpdate()
                        .setIncrBy(Wallet::getAmount, amount)
                        .setIncrBy(Wallet::getIncome,amount)
                        .eq(Wallet::getUid,uid);

            }
            case LARY.WALLET.TRANSFER.POCKET -> {
                lambdaUpdate()
                        .setIncrBy(Wallet::getPocket, amount)
                        .setIncrBy(Wallet::getIncome,amount)
                        .eq(Wallet::getUid,uid);
            }
        }
    }
    private void incr(List<Long> uid,BigDecimal amount,int transfer) {
        switch (transfer) {
            case LARY.WALLET.TRANSFER.AMOUNT -> {
                lambdaUpdate()
                        .setIncrBy(Wallet::getAmount, amount)
                        .setIncrBy(Wallet::getIncome,amount)
                        .in(Wallet::getUid,uid);

            }
            case LARY.WALLET.TRANSFER.POCKET -> {
                lambdaUpdate()
                        .setIncrBy(Wallet::getPocket, amount)
                        .setIncrBy(Wallet::getIncome,amount)
                        .in(Wallet::getUid,uid);
            }
        }
    }
    @Override
    public ResponsePair<Void> systemOutcomeTransfer(SystemOutcomeTransferDTO dto) {

        return transactionTemplate.execute(status -> {
            ResponsePair<List<Wallet>> data = getWallets(dto.getMembers());
            if(data.isFail()) {
                return BusinessKit.fail(data.getMsg());
            }
            List<Wallet> userWallets = data.getData();
            List<WalletIncome> walletIncomes = new ArrayList<>();
            userWallets.forEach(user->{
                WalletIncome out = new WalletIncome()
                        .setUid(user.getUid())
                        .setAmount(dto.getAmount())
                        .setType(dto.getType());
                walletIncomes.add(out);
            });
            List<Long> users = userWallets
                    .stream()
                    .map(Wallet::getUid)
                    .toList();
            walletIncomeService.saveBatch(walletIncomes);
            lambdaUpdate()
                    .setIncrBy(Wallet::getAmount, dto.getAmount())
                    .in(Wallet::getUid,users);
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> systemIncomeTransfer(SystemIncomeTransferDTO dto) {
        return transactionTemplate.execute(status -> {
            ResponsePair<List<Wallet>> data = getWallets(dto.getMembers());
            if(data.isFail()) {
                return BusinessKit.fail(data.getMsg());
            }
            List<Wallet> userWallets = data.getData();
            List<Long> validUsers = userWallets
                    .stream()
                    .filter(u -> u.getAmount().compareTo( dto.getAmount()) > 0)
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
                        out.setAmount(dto.getAmount());
                        out.setType(dto.getType());
                        return out;
            }).toList();
            walletOutcomeService.saveBatch(walletOutcomes);
            lambdaUpdate()
                    .setDecrBy(Wallet::getAmount, dto.getAmount())
                    .in(Wallet::getUid, validUsers);
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<List<Wallet>> getWallets(List<Long> members) {
        if (CollectionKit.isEmpty(members)) {
            return BusinessKit.fail("members null");
        }
        List<Long> users = CollectionKit.removeRepeat(members);
        List<Wallet> userWallets = lambdaQuery()
                .select(Wallet::getAmount,Wallet::getUid)
                .in(Wallet::getUid, users)
                .list();
        if (userWallets.isEmpty()) {
            return BusinessKit.fail("wallet error");
        }
        return BusinessKit.ok(userWallets);
    }


    @Override
    public ResponsePair<BalanceVO> my() {
        Wallet wallet = lambdaQuery()
                .select(Wallet::getAmount,Wallet::getOutcome, Wallet::getIncome)
                .eq(Wallet::getUid, RequestContext.uid())
                .one();
        if (wallet == null) {
            log.error("query wallet error,not found,uid :{}",RequestContext.uid());
            return BusinessKit.fail("wallet not exist");
        }
        return BusinessKit.ok(new BalanceVO(wallet));
    }

}
