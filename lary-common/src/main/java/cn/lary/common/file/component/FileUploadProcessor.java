package cn.lary.common.file.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.file.dto.FileUploadDTO;

public interface FileUploadProcessor {


    ResponsePair<String> execute(FileUploadDTO dto);

}
