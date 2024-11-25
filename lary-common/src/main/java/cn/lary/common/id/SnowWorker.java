package cn.lary.common.id;

import cn.lary.common.exception.SystemException;
import cn.lary.common.kit.SystemClock;

public class SnowWorker implements ISnowWorker {
    /**
     * 基础时间
     */
    protected final long BaseTime;

    /**
     * 机器码
     */
    protected final short WorkerId;

    /**
     * 机器码位长
     */
    protected final byte WorkerIdBitLength;

    /**
     * 数据中心id
     */
    protected final short DataCenterId;

    /**
     * 数据中心id位长
     */
    protected final byte DataCenterIdBitLength;

    /**
     * 自增序列数位长
     */
    protected final byte SeqBitLength;

    /**
     * 最大序列数（含）
     */
    protected final int MaxSeqNumber;

    /**
     * 最小序列数（含）
     */
    protected final short MinSeqNumber;

    /**
     * 最大漂移次数（含）
     */
    protected final int TopOverCostCount;

    /**
     * 锁对象
     */
    protected final static byte[] _SyncLock = new byte[0];

    /**
     * 时间戳位移位
     */
    protected final byte _TimestampShift;

    /**
     * 数据中心id位移
     */
    protected final byte _DataCenterShift;

    /**
     * 当前能使用的序列数id
     */
    protected short _CurrentSeqNumber;
    /**
     * 最后一次生成id的时间戳差值
     */
    protected long _LastTimeTick = 0;
    /**
     * 回拨时间戳差值
     */
    protected long _TurnBackTimeTick = 0;
    /**
     * 回拨序数位索引
     */
    protected byte _TurnBackIndex = 0;
    /**
     * 超出当前毫秒序号最大长度标识
     */
    protected boolean _IsOverCost = false;
    protected int _OverCostCountInOneTerm = 0;
    protected int _GenCountInOneTerm = 0;
    /**
     * 序列数索引，用一个+1，【0,4】预留
     */
    protected int _TermIndex = 0;

    public SnowWorker(IdGenerationOptions options) {
        BaseTime = options.BaseTime != 0 ? options.BaseTime : 1582136402000L;
        WorkerIdBitLength = options.WorkerIdBitLength == 0 ? 6 : options.WorkerIdBitLength;
        WorkerId = options.WorkerId;
        SeqBitLength = options.SeqBitLength == 0 ? 6 : options.SeqBitLength;

        //校验DataCenterId，如果DataCenterIdBitLength为0，那么DataCenterId强制为0
        if (options.DataCenterIdBitLength == 0) {
            DataCenterId = 0;
        } else {
            DataCenterId = options.DataCenterId;
        }
        DataCenterIdBitLength = options.DataCenterIdBitLength;

        MaxSeqNumber = options.MaxSeqNumber <= 0 ? (1 << SeqBitLength) - 1 : options.MaxSeqNumber;
        MinSeqNumber = options.MinSeqNumber;
        TopOverCostCount = options.TopOverCostCount == 0 ? 2000 : options.TopOverCostCount;
        //时间戳位移为 机器码位长 + 数据中心id位长 + 序数号位长
        _TimestampShift = (byte) (WorkerIdBitLength + DataCenterIdBitLength + SeqBitLength);
        //数据中心id位移为 机器码位长 + 数据中心id位长 + 序数号位长
        _DataCenterShift = (byte) (DataCenterIdBitLength + SeqBitLength);
        _CurrentSeqNumber = MinSeqNumber;
    }
    private void BeginOverCostAction(long useTimeTick) {

    }

    private void EndOverCostAction(long useTimeTick) {
        if (_TermIndex > 10000) {
            _TermIndex = 0;
        }
    }

    private void BeginTurnBackAction(long useTimeTick) {

    }

    private void EndTurnBackAction(long useTimeTick) {

    }

    /**
     * 正常的获取下一个id，生成id核心代码
     */
    private long NextNormalId()  {
        long currentTimeTick = GetCurrentTimeTick();

        //如果出现时间回拨
        if (currentTimeTick < _LastTimeTick) {
            if (_TurnBackTimeTick < 1) {
                _TurnBackTimeTick = _LastTimeTick - 1;
                _TurnBackIndex++;

                // 每毫秒序列数的前5位是预留位，0用于手工新值，1-4是时间回拨次序
                // 支持4次回拨次序（避免回拨重叠导致ID重复），可无限次回拨（次序循环使用）。
                if (_TurnBackIndex > 4) {
                    _TurnBackIndex = 1;
                }
                BeginTurnBackAction(_TurnBackTimeTick);
            }

            return CalcTurnBackId(_TurnBackTimeTick);
        }

        // 时间追平时，_TurnBackTimeTick清零
        if (_TurnBackTimeTick > 0) {
            EndTurnBackAction(_TurnBackTimeTick);
            _TurnBackTimeTick = 0;
        }

        if (currentTimeTick > _LastTimeTick) {
            _LastTimeTick = currentTimeTick;
            _CurrentSeqNumber = MinSeqNumber;

            return CalcId(_LastTimeTick);
        }

        //当前序列数
        if (_CurrentSeqNumber > MaxSeqNumber) {
            BeginOverCostAction(currentTimeTick);

            _TermIndex++;
            _LastTimeTick++;
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = true;
            _OverCostCountInOneTerm = 1;
            _GenCountInOneTerm = 1;

            return CalcId(_LastTimeTick);
        }

        return CalcId(_LastTimeTick);
    }

    /**
     * 超出该毫秒内的支持的生成数，生成id
     * @return long生产的id
     */
    private long NextOverCostId() {
        long currentTimeTick = GetCurrentTimeTick();

        //如果出现时间回拨
        if (currentTimeTick > _LastTimeTick) {
            EndOverCostAction(currentTimeTick);

            _LastTimeTick = currentTimeTick;
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = false;
            _OverCostCountInOneTerm = 0;
            _GenCountInOneTerm = 0;

            return CalcId(_LastTimeTick);
        }


        if (_OverCostCountInOneTerm >= TopOverCostCount) {
            EndOverCostAction(currentTimeTick);

            _LastTimeTick = GetNextTimeTick();
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = false;
            _OverCostCountInOneTerm = 0;
            _GenCountInOneTerm = 0;

            return CalcId(_LastTimeTick);
        }

        if (_CurrentSeqNumber > MaxSeqNumber) {
            _LastTimeTick++;
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = true;
            _OverCostCountInOneTerm++;
            _GenCountInOneTerm++;

            return CalcId(_LastTimeTick);
        }

        _GenCountInOneTerm++;
        return CalcId(_LastTimeTick);
    }

    /**
     * 正常情况下，采用左位移拼接结果id
     *
     * @param useTimeTick 时间戳差值
     * @return 生成的id
     */
    private long CalcId(long useTimeTick) {
        long result = ShiftStitchingResult(useTimeTick);
        _CurrentSeqNumber++;
        return result;
    }

    /**
     * 发生时间回拨的时候，采用左位移拼接结果id
     *
     * @param useTimeTick 时间戳差值
     * @return 生成的id
     */
    private long CalcTurnBackId(long useTimeTick) {
        long result = ShiftStitchingResult(useTimeTick);
        _TurnBackTimeTick--;
        return result;
    }

    /**
     * 左位移拼接返回的id
     *
     * @param useTimeTick 时间差值
     * @return 生成的id
     */
    protected long ShiftStitchingResult(long useTimeTick) {
        /**
         采用BigInteger重构，但是并发量可能会低，需要测试
         return BigInteger.valueOf(useTimeTick)
         .shiftLeft(_TimestampShift).add(BigInteger.valueOf(DataCenterId))
         .shiftLeft(_DataCenterShift).add(BigInteger.valueOf(WorkerId))
         .shiftLeft(SeqBitLength).add(BigInteger.valueOf(_CurrentSeqNumber));
         **/

        return ((useTimeTick << _TimestampShift) + //时间差值，时间戳位移 = 数据中心id位长 + 机器码位长 + 序数位长
                ((long) DataCenterId << _DataCenterShift) + //数据中心id，数据中心id位移 = 机器码位长 + 序数位长
                ((long) WorkerId << SeqBitLength) + //机器码数，机器码位移 = 序数位长
                (int) _CurrentSeqNumber);
    }

    /**
     * 获取当前时间 - 系统时间差值
     *
     * @return 时间差值
     */
    protected long GetCurrentTimeTick() {
        long millis = SystemClock.now();
        return millis - BaseTime;
    }

    /**
     * 获取下次时间差值
     *
     * @return 时间差值
     */
    protected long GetNextTimeTick() {
        long tempTimeTicker = GetCurrentTimeTick();
        while (tempTimeTicker <= _LastTimeTick) {
            try {
                Thread.sleep(10); //发生回拨等待3毫秒，实际上是阻塞10毫秒生成
            } catch (InterruptedException e) {
                throw new SystemException("Error when time callback waits three millisecond");
            }
            tempTimeTicker = GetCurrentTimeTick();
        }

        return tempTimeTicker;
    }

    /**
     * 真正执行的方法，判断是否超出当前生成序数的最大值，执行不同的方法
     *
     * @return 生成的id
     */
    @Override
    public long next() {
        synchronized (_SyncLock) {
            return _IsOverCost ? NextOverCostId() : NextNormalId();
        }
    }
}
