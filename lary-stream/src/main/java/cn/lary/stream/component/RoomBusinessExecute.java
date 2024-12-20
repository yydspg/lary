package cn.lary.stream.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.stream.dto.GoLiveDTO;
import cn.lary.stream.service.RoomService;
import cn.lary.api.stream.vo.DownLiveVO;
import cn.lary.api.stream.vo.GoLiveVO;
import cn.lary.api.stream.vo.JoinLiveVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomBusinessExecute {

    private final RoomService roomService;


    /**
     * 加入直播间
     * @param toUid anchor uid
     * @param ip ip
     * @return {@link JoinLiveVO}
     */
    public ResponsePair<JoinLiveVO> join( long toUid,long sid, String ip){
            return roomService.join(toUid,sid,ip);
    }

    /**
     * 加入直播间
     * @param ip ip
     * @param dto {@link GoLiveDTO}
     * @return {@link GoLiveVO}
     */
    public ResponsePair<GoLiveVO> go( String ip, GoLiveDTO dto) {
        return roomService.go(ip,dto);
    }

    /**
     * 结束直播
     * @return {@link DownLiveVO}
     */
    public ResponsePair<DownLiveVO> end() {
        return roomService.end();
    }

    /**
     * 离开直播间
     * @return OK
     */
    public ResponsePair<Void> leave() {
        return roomService.leave();
    }



    /**
     * block接口,这里不打算实现这个逻辑的原因是，如果要手动下线用户
     * 就要维护srs节点，所以现在的情况是在 mq检查连接的时候，判断用户关系
     * todo : 存在直接封掉用户问题，必须要将控制权反转，所以必须要维护这个关系
     * @param toUid 被封禁用户
     * @return OK
     */
    @Deprecated
    public ResponsePair<Void> block(long uid, long toUid) {
        throw new RuntimeException("");
    }
}



