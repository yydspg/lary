package cn.lary.common.file.component;

import cn.lary.common.business.BusinessSign;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.exception.Assert;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.file.dto.FileUploadDTO;

import java.util.ArrayList;
import java.util.Collection;


public abstract class AbstractFileUploadProcessor implements FileUploadProcessor , BusinessSign {

    private PresignedFileUploadUrlBuilder builder;

    private int sign;

    private final Collection<FileUploadExecutionListener> fileUploadExecutionListeners =  new ArrayList<>();

    @Override
    public ResponsePair<String> execute(FileUploadDTO dto) {
        Assert.notNull(builder);
        Assert.notNull(fileUploadExecutionListeners);
        ResponsePair<Void> response = begin(dto);
        if (response.isFail()) {
            return BusinessKit.fail(response.getMsg());
        }
        FileUploadBusinessConfig pair = new FileUploadBusinessConfig()
                .setBusiness(sign)
                .setFileName(dto.getName())
                .setExpire(getExpire())
                .setBucketName(getBucketName());
        String url = builder.build(pair);
        end(dto);
        return BusinessKit.ok(url);
    }

    /**
     * 初始化
     * @param builder b
     * @param listeners l
     */
    protected final void initProcessor(PresignedFileUploadUrlBuilder builder,Collection<FileUploadExecutionListener> listeners){
        setBuilder(builder);
        setFileUploadExecutionListeners(listeners);
        setSign(getSign());
    }

    protected final ResponsePair<Void> begin(FileUploadDTO dto){
        fileUploadExecutionListeners.forEach(listener->{listener.beforeBegin(dto);});
        return doBegin(dto);
    }
    protected final void end(FileUploadDTO dto){
        doEnd(dto);
        fileUploadExecutionListeners.forEach(listener->{listener.afterEnd(dto);});
    }
    /**
     * 业务侧执行
     * @param dto {FileUploadDTO}
     */
    protected abstract ResponsePair<Void> doBegin(FileUploadDTO dto);
    /**
     * 业务侧执行
     * @param dto {FileUploadDTO}
     */
    protected abstract void doEnd(FileUploadDTO dto);

    /**
     * 业务侧动态bucket name
     * @return s
     */
    protected abstract String getBucketName();

    /**
     * 业务侧 动态过期时间
     * @return time
     */
    protected abstract int getExpire();


    public final void setFileUploadExecutionListeners(Collection<FileUploadExecutionListener> fileUploadExecutionListeners){
        this.fileUploadExecutionListeners.addAll(fileUploadExecutionListeners);
    }

    public final void setBuilder(PresignedFileUploadUrlBuilder builder){
        this.builder = builder;
    }


    public final PresignedFileUploadUrlBuilder getBuilder() {
        return builder;
    }

    public final Collection<FileUploadExecutionListener> getFileUploadExecutionListeners() {
        return fileUploadExecutionListeners;
    }
    public final void setSign(int sign) {
        this.sign = sign;
    }
}
