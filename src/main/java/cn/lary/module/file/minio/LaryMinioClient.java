package cn.lary.module.file.minio;

import cn.lary.core.exception.Assert;
import cn.lary.module.file.component.FileUploadBusinessConfig;
import cn.lary.module.file.component.PresignedFileUploadUrlBuilder;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LaryMinioClient implements PresignedFileUploadUrlBuilder {

    private static final String endpoint = "https://minio.s3.amazonaws.com";
    private static final String accessKey = "test";
    private static final String secretKey = "test";

    private static MinioClient minioClient;

    public LaryMinioClient(){
        init();
    }
    @Override
    public String build(FileUploadBusinessConfig pair){
        Assert.notNull(pair);
        Assert.notNull(minioClient);
        String url = null;
        try {
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .expiry(pair.getExpire())
                            .bucket(pair.getBucketName())
                            .object(pair.getFileName())
                            .method(Method.PUT)
                    .build());
        }catch (Exception e){
            log.error("build file upload url error,message:{}",e.getMessage());
        }
        return url;
    }

    public void init(){
        try {
            minioClient = MinioClient
                    .builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (Exception e) {
            log.error("lary ==> init minio client error,message:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
