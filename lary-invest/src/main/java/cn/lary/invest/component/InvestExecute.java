package cn.lary.invest.component;

import cn.lary.invest.service.InvestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestExecute {

    private final InvestService investService;

}
