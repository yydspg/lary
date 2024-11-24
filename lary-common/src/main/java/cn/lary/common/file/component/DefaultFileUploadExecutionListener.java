package cn.lary.common.file.component;

import cn.lary.common.file.dto.FileUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultFileUploadExecutionListener implements FileUploadExecutionListener {

    @Override
    public void beforeBegin(FileUploadDTO dto) {
        log.info("component process before begin");
    }


    @Override
    public void afterEnd(FileUploadDTO dto) {
        log.info("component process after end");
    }
}
