package cn.lary.module.file.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.file.dto.FileUploadDTO;

public interface FileUploadProcessor {


    ResponsePair<String> execute(FileUploadDTO dto);

}
