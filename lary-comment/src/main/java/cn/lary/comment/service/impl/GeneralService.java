package cn.lary.comment.service.impl;

import cn.lary.api.comment.dto.BehaviorProcessorDTO;
import cn.lary.api.message.YutakMessageService;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.StringKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralService {

//    private final UserService userService;
//    private final YutakMessageService messageService;

    public ResponsePair<Void> processUserMention(String mentions, String content,long eid){
        if (StringKit.isEmpty(mentions)){
            return BusinessKit.ok();
        }
//        String[] users = StringKit.split(mentions, ",");
//        if(users == null || users.length == 0){
//            return BusinessKit.fail("mentions invalid");
//        }
//        List<Long> ids = Arrays.stream(users).map(Long::valueOf).distinct().toList();
//        if (CollectionKit.isEmpty(ids)) {
//            return BusinessKit.fail("mentions invalid");
//        }
//        List<Long> mentionUsers = userService.lambdaQuery()
//                .select(User::getUid,User::getStatus)
//                .in(User::getUid, ids)
//                .list()
//                .stream()
//                .filter(t -> t.getStatus() == USER.STATUS.OK)
//                .distinct()
//                .map(User::getUid)
//                .toList();
//        MentionNotifyPayload payload = new MentionNotifyPayload()
//                .setEid(eid)
//                .setContent(content)
//                .setTimestamp(SystemClock.now());
//        messageService.send(new CommentMentionMessage(mentionUsers, RequestContext.uid(),payload));
        return BusinessKit.ok();
    }

    public ResponsePair<Void> processCommentBehavior(BehaviorProcessorDTO dto){
//        //here need check
//        messageService.syncSendRocketMessage(new BehaviorMessage()
//                .setCategory(dto.getCategory())
//                .setCid(dto.getCid()));
        return BusinessKit.ok();
    }
}
