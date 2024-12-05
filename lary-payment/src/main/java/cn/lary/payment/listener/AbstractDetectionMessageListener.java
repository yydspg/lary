package cn.lary.payment.listener;



import cn.lary.api.payment.dto.PaymentQueryProcessPair;
import cn.lary.payment.plugin.PaymentPluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbstractDetectionMessageListener {

//    private final EventService eventService;
    private final PaymentPluginManager paymentPluginManager;

    public void process(AbstractActiveDetectionMessage message){
//        Event event = eventService.getById(message.getEventId());
//        if (event == null) {
//            log.error("search event error,eventId:{}", message.getEventId());
//            return;
//        }
//        if (event.getStatus() == LARY.EVENT.STATUS.COMMIT ||
//                event.getStatus() == LARY.EVENT.STATUS.FAIL){
//            return ;
//        }
        // not process
        PaymentQueryProcessPair pair = new PaymentQueryProcessPair()
                .setPlugin(message.getPlugin())
                .setBusinessSign(message.getBusinessSign())
                .setClient(message.getClient())
                .setPaymentId(message.getPaymentId());
        paymentPluginManager.doQuery(pair);
    }
}
