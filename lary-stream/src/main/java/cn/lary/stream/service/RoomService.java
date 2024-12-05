package cn.lary.stream.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.stream.dto.GoLiveDTO;
import cn.lary.api.stream.entity.Room;
import cn.lary.api.stream.vo.DownLiveVO;
import cn.lary.api.stream.vo.GoLiveVO;
import cn.lary.api.stream.vo.JoinLiveVO;
import cn.lary.api.stream.vo.RoomVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface RoomService extends IService<Room> {

    /**
     * 加入直播间
     * @param toUid 主播id
     * @param ip 用户ip
     * @return {@link JoinLiveVO}
     */
     ResponsePair<JoinLiveVO> join( long toUid,long sid, String ip);

    /**
     * 离开直播间
     * @return OK
     */
     ResponsePair<Void> leave();
    /**
     * 结束直播
     *
     * @return {@link DownLiveVO}
     */
    ResponsePair<DownLiveVO> end();

    /**
     * 开启直播
     *
     * @param ip  ip
     * @param dto {@link GoLiveDTO}
     * @return {@link GoLiveVO}
     */
    ResponsePair<GoLiveVO> go(String ip, GoLiveDTO dto);

//    /**
//     * 判断是否是热门主播
//     * @param room {@link Room}
//     * @return OK
//     */
//    boolean isHot(Room room) ;


    ResponsePair<List<RoomVO>> show();
}