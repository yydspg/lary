package cn.lary.module.wallet.service.impl;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.module.wallet.dto.BatchOutcomeTransferDTO;
import cn.lary.module.wallet.dto.TransferDTO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.entity.WalletIncome;
import cn.lary.module.wallet.entity.WalletOutcome;
import cn.lary.module.wallet.mapper.WalletMapper;
import cn.lary.module.wallet.service.WalletIncomeService;
import cn.lary.module.wallet.service.WalletOutcomeService;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.handler.codec.compression.Bzip2Decoder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final WalletMapper walletMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResPair<Void> transfer(TransferDTO dto) {
        Wallet uidWallet = walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, dto.getUid())
                .eq(Wallet::getIsBlock, false));
        if (uidWallet == null) {
            return BizKit.fail("uid wallet not exist");
        }
        Wallet toUidWallet = walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, dto.getToUid())
                .eq(Wallet::getIsBlock, false));
        if (toUidWallet == null) {
            return BizKit.fail("to uid wallet not exist");
        }
        if (uidWallet.getVcFee() - uidWallet.getVcLocked() < dto.getAmount()) {
            return BizKit.fail("uid balance not enough");
        }
        //update
        walletMapper.update(new LambdaUpdateWrapper<Wallet>()
                .eq(Wallet::getUid, dto.getUid())
                .set(Wallet::getVcFee, uidWallet.getVcFee() - dto.getAmount())
                .set(Wallet::getVcOutcome, uidWallet.getVcOutcome() + dto.getAmount()));
        walletMapper.update(new LambdaUpdateWrapper<Wallet>()
                .eq(Wallet::getUid, dto.getToUid())
                .set(Wallet::getVcFee,toUidWallet.getVcFee() + dto.getAmount())
                .set(Wallet::getVcIncome,toUidWallet.getVcIncome() + dto.getAmount()));
        walletIncomeService.save(WalletIncome.of(dto));
        walletOutcomeService.save(WalletOutcome.of(dto));
        return BizKit.ok();
    }

    @Override
    @Transactional
    public ResPair<Void> batchOutcomeTransfer(BatchOutcomeTransferDTO dto) {
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
        Wallet uidWallet = walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, dto.getUid())
                .eq(Wallet::getIsBlock, false));
        if (uidWallet == null) {
            return BizKit.fail("uid wallet not exist");
        }
        // outcome
        List<WalletIncome> walletIncomes = new ArrayList<>();
        List<WalletOutcome> walletOutcomes = new ArrayList<>();

        dto.getRecipients().forEach(uid->{
            WalletOutcome out = new WalletOutcome().setUid(dto.getUid())
                    .setToUid(uid)
                    .setCost(dto.getAmount())
                    .setType(dto.getType())
                    .setChannelId(dto.getChannelId())
                    .setChannelType(dto.getChannelType());
            walletOutcomes.add(out);
            WalletIncome in = new WalletIncome().setUid(uid)
                    .setToUid(dto.getUid())
                    .setCost(dto.getAmount())
                    .setType(dto.getType())
                    .setChannelId(dto.getChannelId())
                    .setChannelType(dto.getChannelType());
            walletIncomes.add(in);
        });
        walletOutcomeService.saveBatch(walletOutcomes);
        walletIncomeService.saveBatch(walletIncomes);
        walletMapper.update(new LambdaUpdateWrapper<Wallet>()
                .set(Wallet::getVcFee,uidWallet.getVcFee() - dto.getTotalAmount())
                .eq(Wallet::getUid,dto.getUid()));
        // tip,mp setIncrBy() method  since 3.5.6
        walletMapper.update(new LambdaUpdateWrapper<Wallet>()
                .setIncrBy(Wallet::getVcFee,dto.getAmount())
                .in(Wallet::getUid,dto.getRecipients()));
        return BizKit.ok();
    }
}
