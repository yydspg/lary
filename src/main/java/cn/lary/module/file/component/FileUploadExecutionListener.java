package cn.lary.module.file.component;

import cn.lary.module.file.dto.FileUploadDTO;

public interface FileUploadExecutionListener {


    void beforeBegin(FileUploadDTO dto);

    void afterEnd(FileUploadDTO dto);
}
