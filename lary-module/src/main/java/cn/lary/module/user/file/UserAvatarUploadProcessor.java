package cn.lary.module.user.file;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.file.component.AbstractFileUploadProcessor;
import cn.lary.module.file.component.FileUploadExecutionListener;
import cn.lary.module.file.component.PresignedFileUploadUrlBuilder;
import cn.lary.module.file.dto.FileUploadDTO;
import cn.lary.module.user.dto.UserAvatarUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Slf4j
@Component
public class UserAvatarUploadProcessor extends AbstractFileUploadProcessor {

    @Autowired
    public UserAvatarUploadProcessor(PresignedFileUploadUrlBuilder builder, Collection<FileUploadExecutionListener> listeners) {
        initProcessor(builder, listeners);
    }

    @Override
    protected ResponsePair<Void> doBegin(FileUploadDTO dto) {
        UserAvatarUploadDTO avatarDTO= (UserAvatarUploadDTO) dto;
        log.info("user avatar upload begin");
        return BusinessKit.ok();
    }

    @Override
    protected void doEnd(FileUploadDTO dto) {
            log.info("user avatar upload process end");
    }

    @Override
    protected String getBucketName() {
        return "lary:user:avatar";
    }

    @Override
    protected int getExpire() {
        return 60*5;
    }


    @Override
    public int getSign() {
        return LARY.BUSINESS.FILE_UPLOAD.USER_AVATAR;
    }
}
