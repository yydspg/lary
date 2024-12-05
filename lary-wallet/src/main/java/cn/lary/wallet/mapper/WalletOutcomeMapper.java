package cn.lary.wallet.mapper;

import cn.lary.api.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.api.wallet.entity.WalletOutcome;
import cn.lary.api.wallet.vo.WalletOutcomeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
public interface WalletOutcomeMapper extends BaseMapper<WalletOutcome> {

    List<WalletOutcomeVO> getOutcomes(WalletOutcomePageQueryDTO dto);
}
