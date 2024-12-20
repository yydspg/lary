package cn.lary.common.id;

public class CallbackOptions {

    /**
     * 事件类型
     * 1-开始，2-结束，8-漂移
     */
    public int ActionType = 0;

    /**
     * 时间戳
     */
    public long TimeTick = 0;

    /**
     * 机器码
     */
    public short WorkerId = 0;

    /**
     *
     */
    public int OverCostCountInOneTerm = 0;

    /**
     * 漂移期间生产ID个数
     */
    public int GenCountInOneTerm = 0;

    /**
     * 漂移周期
     */
    public int TermIndex = 0;
    public CallbackOptions(short workerId, long timeTick, int actionType, int overCostCountInOneTerm, int genCountWhenOverCost, int index) {
        ActionType = actionType;
        TimeTick = timeTick;
        WorkerId = workerId;
        OverCostCountInOneTerm = overCostCountInOneTerm;
        GenCountInOneTerm = genCountWhenOverCost;
        TermIndex = index;
    }
}
