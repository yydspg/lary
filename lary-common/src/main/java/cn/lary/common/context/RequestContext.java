package cn.lary.common.context;

public class RequestContext {

    private static final ThreadLocal<Pair> currentRequest = new ThreadLocal<>();

    public static void setCurrent(Pair data) {
        currentRequest.set(data);
    }

    protected static Pair getCurrent() {
        return currentRequest.get();
    }

    public static void removeCurrent() {
        currentRequest.remove();
    }

    /**
     * this method use thread local ,can not return null
     * @return uid
     */
    public static long getLoginUID() {
        return  getCurrent().uid;
    }

    /**
     * this method use thread local ,can not return null
     * @return login username
     */
    public static String getLoginName() {
        return  getCurrent().name;
    }
}