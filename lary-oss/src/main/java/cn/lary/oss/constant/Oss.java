package cn.lary.oss.constant;

/**
 * @author paul 2024/4/17
 */

public interface Oss {
    public interface Channel{
        public String MINIO = "minio";
        public String ALIYUN = "aliyun";
    }
    public interface Limit {
        long MAX_OBJECT_SIZE = 5L * 1024 * 1024 * 1024 * 1024;
        int MIN_MULTIPART_SIZE = 5 * 1024 * 1024;
        long MAX_PART_SIZE = 5L * 1024 * 1024 * 1024;
        int MAX_MULTIPART_COUNT = 10000;
    }
    public interface ErrorCode {
        String OSS_CLIENT_POOL_ERROR ="无法从Oss对象池中获取对象";
        String OSS_ERROR_RESPONSE ="对象存储服务器返回错误响应";
        String OSS_INSUFFICIENT_DATA ="对象存储服务器返回数据不足";
        String OSS_INTERNAL ="对象存储服务器内部错误";
        String OSS_INVALID_KEY ="对象存储使用无效的秘钥";
        String OSS_INVALID_RESPONSE ="对象存储返回无效的响应";
        String OSS_IO ="对象存储出现IO错误";
        String OSS_NO_SUCH_ALGORITHM ="使用对象存储不支持算法错误";
        String OSS_SERVER ="对象存储服务器出现错误";
        String OSS_XML_PARSER ="对象存储 XML 解析出现错误";
        String OSS_EXECUTION ="对象存储服务器异步执行错误";
        String OSS_INTERRUPTED ="对象存储服务器异步执行中断错误";
        String OSS_BUCKET_POLICY_TOO_LARGE ="存储桶访问策略过大";
        String OSS_INVALID_CIPHER_TEXT ="无效密码文本错误";
        String OSS_CONNECTION = "Minio 服务器无法访问或未启动";
    }
    public interface FragProcessState{
        byte INIT = 0;
        byte OVER = 1;
        byte ABORT = 2;
    }
}
