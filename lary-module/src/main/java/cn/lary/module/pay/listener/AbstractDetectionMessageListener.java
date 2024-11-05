package cn.lary.module.pay.listener;


import cn.lary.module.common.entity.Event;
import cn.lary.module.common.service.EventService;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.PaymentQueryProcessPair;
import cn.lary.module.pay.plugin.PaymentPluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbstractDetectionMessageListener {

    private final EventService eventService;
    private final PaymentPluginManager paymentPluginManager;

    public void process(AbstractActiveDetectionMessage message){
        Event event = eventService.getById(message.getEventId());
        if (event == null) {
            log.error("search event error,eventId:{}", message.getEventId());
            return;
        }
        if (event.getStatus() == LARY.EVENT.STATUS.COMMIT ||
                event.getStatus() == LARY.EVENT.STATUS.FAIL){
            return ;
        }
        // not process
        PaymentQueryProcessPair pair = new PaymentQueryProcessPair()
                .setPlugin(message.getPlugin())
                .setBusinessSign(message.getBusinessSign())
                .setClient(message.getClient())
                .setPaymentId(message.getPaymentId());
        paymentPluginManager.doQuery(pair);
    }
}
