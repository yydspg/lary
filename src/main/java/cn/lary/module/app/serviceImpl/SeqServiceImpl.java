package cn.lary.module.app.serviceImpl;

import cn.lary.core.exception.BizException;
import cn.lary.module.app.entity.Seq;
import cn.lary.module.app.entity.SeqBase;
import cn.lary.module.app.mapper.SeqMapper;
import cn.lary.module.app.service.SeqService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-14
 */
@Service
public class SeqServiceImpl extends ServiceImpl<SeqMapper, Seq> implements SeqService {
    private final int step = 1000;
    private final ConcurrentHashMap<String, SeqBase> map = new ConcurrentHashMap<>();
    @Override
    public long build(String seqKey) {
        SeqBase seqB = map.get(seqKey);
        if (seqB == null) {
            Seq seq = baseMapper.selectOne(new LambdaQueryWrapper<Seq>().eq(Seq::getKey, seqKey));
            if (seq == null) {
                long currentStep = 1000;
                seq = new Seq().setKey(seqKey).setStep(step).setMinSeq(currentStep+step);
                int id = baseMapper.insert(seq);
                seqB = new SeqBase().setCurrentSeq(new AtomicLong(currentStep)).setMaxSeq(currentStep+step).setId(id).setStep(step);
            }else {
                seqB = new SeqBase().setMaxSeq(seq.getStep()+seq.getMinSeq()).setCurrentSeq(new AtomicLong(seq.getMinSeq())).setStep(seq.getStep()).setId(seq.getId());
            }
            map.put(seqKey, seqB);
        }
        if (seqB.getCurrentSeq().get() > seqB.getMaxSeq()) {
            // overflow
            Seq updateRecord = new Seq().setKey(seqKey).setMinSeq(seqB.getMaxSeq() + seqB.getStep()).setId(seqB.getId());
            baseMapper.updateById(updateRecord);
            seqB.getCurrentSeq().set(seqB.getMaxSeq() + seqB.getStep());
        }
        return seqB.getCurrentSeq().getAndIncrement();
    }
}
