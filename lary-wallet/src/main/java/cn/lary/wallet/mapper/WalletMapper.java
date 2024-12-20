package cn.lary.wallet.mapper;

import cn.lary.api.wallet.entity.BatchOutcomeRandomTransfer;
import cn.lary.api.wallet.entity.Wallet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-09-13
 */
public interface WalletMapper extends BaseMapper<Wallet> {


    void batchOutcomeRandomTransfer( List<BatchOutcomeRandomTransfer> transfer);

}
