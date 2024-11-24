package cn.lary.wallet.mapper;

import cn.lary.wallet.entity.BatchOutcomeRandomTransfer;
import cn.lary.wallet.entity.Wallet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
