package cn.lary.common.context;

public class RequestContext {

    private static final ThreadLocal<Profile> request = new ThreadLocal<>();

    public static void setCurrent(Profile profile) {
        request.set(profile);
    }

    protected static Profile getCurrent() {
        return request.get();
    }

    public static void removeCurrent() {
        request.remove();
    }

    /**
     * this method use thread local ,can not return null
     * @return uid
     */
    public  static long uid() {
        return  getCurrent().getUid();
    }

    /**
     * this method use thread local ,can not return null
     * @return login username
     */
    public static String name() {
        return  getCurrent().getName();
    }

    /**
     * this method use thread local ,can not return null
     * @return device flag
     */
    public static int flag() {
        return  getCurrent().getFlag();
    }
}