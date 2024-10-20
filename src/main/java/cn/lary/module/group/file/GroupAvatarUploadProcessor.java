package cn.lary.module.group.file;

import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.file.component.AbstractFileUploadProcessor;
import cn.lary.module.file.component.FileUploadExecutionListener;
import cn.lary.module.file.component.PresignedFileUploadUrlBuilder;
import cn.lary.module.file.dto.FileUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class GroupAvatarUploadProcessor extends AbstractFileUploadProcessor {

    @Autowired
    public GroupAvatarUploadProcessor(PresignedFileUploadUrlBuilder builder,
                                      Collection<FileUploadExecutionListener> listeners) {
        initProcessor(builder, listeners);
    }

    @Override
    protected ResponsePair<Void> doBegin(FileUploadDTO dto) {
        return BusinessKit.ok();
    }

    @Override
    protected void doEnd(FileUploadDTO dto) {

    }

    @Override
    protected String getBucketName() {
        return "lary:group:avatar";
    }

    @Override
    protected int getExpire() {
        return 60*3;
    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.FILE_UPLOAD.GROUP_AVATAR;
    }
}
