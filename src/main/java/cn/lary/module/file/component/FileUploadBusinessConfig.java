package cn.lary.module.file.component;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileUploadBusinessConfig {

    private String bucketName;

    private int business;

    private String fileName;

    private int expire;
}
