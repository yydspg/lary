package cn.lary.rpc.kit;

public class ServiceKit {
    public static String buildServiceKey(String name,String version) {
        return  new StringBuilder(name)
                .append(":")
                .append(version)
                .toString();
    }
}
