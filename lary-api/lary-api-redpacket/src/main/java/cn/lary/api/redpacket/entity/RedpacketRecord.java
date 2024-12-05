package cn.lary.api.redpacket.entity;

import cn.lary.api.redpacket.constant.REDPACKET;
import cn.lary.api.redpacket.dto.RedpacketTokenDTO;
import cn.lary.common.kit.SystemClock;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("redpacket_record")
public class RedpacketRecord implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 记录id
     */
    private Long rid;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 直播id
     */
    private Long sid;

    /**
     * 红包金额
     */
    private BigDecimal amount;

    /**
     * 同步状态
     */
    private Integer sync;

    private Long createAt;

    public RedpacketRecord(){}

    public RedpacketRecord(RedpacketTokenDTO dto){
        rid = dto.getRid();
        uid = dto.getUid();
        sid=  dto.getSid();
        amount = dto.getAmount();
        sync = REDPACKET.STATUS.COMMIT;
        createAt = SystemClock.now();
    }
}
