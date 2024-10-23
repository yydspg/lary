package cn.lary.module.report.core;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportBizExecute {

    private final ReportService reportService;

    public ResponsePair<Void> report(String uid) {
        return null;
    }
}
