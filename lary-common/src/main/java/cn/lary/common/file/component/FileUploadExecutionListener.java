package cn.lary.common.file.component;

import cn.lary.common.file.dto.FileUploadDTO;

public interface FileUploadExecutionListener {


    void beforeBegin(FileUploadDTO dto);

    void afterEnd(FileUploadDTO dto);
}
