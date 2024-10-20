package cn.lary.module.file.component;

public interface PresignedFileUploadUrlBuilder {

    String build(FileUploadBusinessConfig pair);
}
