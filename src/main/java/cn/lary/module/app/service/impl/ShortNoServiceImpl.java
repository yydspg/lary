package cn.lary.module.app.service.impl;

import cn.lary.module.app.entity.ShortNo;
import cn.lary.module.app.mapper.ShortNoMapper;
import cn.lary.module.app.service.ShortNoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortNoServiceImpl extends ServiceImpl<ShortNoMapper, ShortNo> implements ShortNoService {
    private final ReentrantLock lock = new ReentrantLock();
    private final ShortNoMapper shortNoMapper;

    @Override
    public String getShortNo() {
        lock.lock();
        try {
            // query
            LambdaQueryWrapper<ShortNo> lw = new LambdaQueryWrapper<>();
            //sql : select * from short_no where used = 0 and hold = 0 and locked = 0 limit 1
            lw.eq(ShortNo::getUsed,0).eq(ShortNo::getHold,0).eq(ShortNo::getLocked,0);
            ShortNo shortNo = shortNoMapper.selectOne(lw);
            if (shortNo == null) {
                log.error("no available short no");
                return null;
            }
            lw.clear();
            // update short locked num
            LambdaUpdateWrapper<ShortNo> uw = new LambdaUpdateWrapper<>();
            uw.set(ShortNo::getLocked,1);
            uw.eq(ShortNo::getShortNo,shortNo.getShortNo());
            shortNoMapper.update(shortNo,uw);
            return shortNo.getShortNo();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void setShortNoUsed(String shortNo, String business) {
        LambdaUpdateWrapper<ShortNo> uw = new LambdaUpdateWrapper<ShortNo>().eq(ShortNo::getShortNo, shortNo).set(ShortNo::getUsed, 1).set(ShortNo::getBusiness, business);
        baseMapper.update(uw);
    }
}
