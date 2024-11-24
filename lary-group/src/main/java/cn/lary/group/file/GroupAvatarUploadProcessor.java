package cn.lary.group.file;

import cn.lary.common.constant.LARY;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.file.component.AbstractFileUploadProcessor;
import cn.lary.common.file.component.FileUploadExecutionListener;
import cn.lary.common.file.component.PresignedFileUploadUrlBuilder;
import cn.lary.common.file.dto.FileUploadDTO;
import cn.lary.common.kit.BusinessKit;
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
