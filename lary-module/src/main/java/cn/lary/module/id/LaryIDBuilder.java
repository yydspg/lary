package cn.lary.module.id;


import cn.lary.common.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LaryIDBuilder implements IdGenerator {

    private static ISnowWorker _SnowWorker = null;

    public LaryIDBuilder(IdGenerationOptions options) throws SystemException {
        // 1.BaseTime
        if (options.BaseTime < 315504000000L || options.BaseTime > System.currentTimeMillis()) {
            throw new SystemException("BaseTime error.");
        }

        // 2.WorkerIdBitLength
        if (options.WorkerIdBitLength <= 0) {
            throw new SystemException("WorkerIdBitLength error.(range:[1, 21])");
        }
        if (options.WorkerIdBitLength + options.SeqBitLength + options.DataCenterIdBitLength > 22) {
            throw new SystemException("error：WorkerIdBitLength + SeqBitLength + DataCenterIdBitLength <= 22");
        }

        // 3.WorkerId
        int maxWorkerIdNumber = (1 << options.WorkerIdBitLength) - 1;
        if (maxWorkerIdNumber == 0) {
            maxWorkerIdNumber = 63;
        }
        if (options.WorkerId < 0 || options.WorkerId > maxWorkerIdNumber) {
            throw new SystemException("WorkerId error. (range:[0, " + (maxWorkerIdNumber > 0 ? maxWorkerIdNumber : 63) + "]");
        }

        // 4. DataCenterId
        int maxDataCenterId = (1 << options.DataCenterIdBitLength) - 1;
        if (options.DataCenterId < 0 || options.DataCenterId > maxDataCenterId){
            throw new SystemException("DataCenterId error. (range:[0,"+ maxDataCenterId + "])");
        }


        // 5.SeqBitLength
        if (options.SeqBitLength < 2 || options.SeqBitLength > 21) {
            throw new SystemException("SeqBitLength error. (range:[2, 21])");
        }

        // 6.MaxSeqNumber
        int maxSeqNumber = (1 << options.SeqBitLength) - 1;
        if (maxSeqNumber == 0) {
            maxSeqNumber = 63;
        }
        if (options.MaxSeqNumber < 0 || options.MaxSeqNumber > maxSeqNumber) {
            throw new SystemException("MaxSeqNumber error. (range:[1, " + maxSeqNumber + "]");
        }

        // 7.MinSeqNumber
        if (options.MinSeqNumber < 5 || options.MinSeqNumber > maxSeqNumber) {
            throw new SystemException("MinSeqNumber error. (range:[5, " + maxSeqNumber + "]");
        }

        _SnowWorker = new SnowWorker(options);

        if (options.Method == 1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("interrupted.", e);
            }
        }
    }
    @Override
    public long next() {
        return _SnowWorker.next();
    }
}
