package cn.lary.module.wallet.mapper;

import cn.lary.module.wallet.entity.BatchOutcomeRandomTransfer;
import cn.lary.module.wallet.entity.Wallet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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
