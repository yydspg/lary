package cn.lary.module.report.core;

import cn.lary.core.dto.ResPair;
import cn.lary.module.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportBizExecute {

    private final ReportService reportService;

    public ResPair<Void> report(String uid) {
        return null;
    }
}
