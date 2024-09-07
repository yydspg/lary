package cn.lary.pkg.srs.serviceImpl;

import cn.lary.kit.StringKit;
import cn.lary.pkg.srs.config.SrsConfig;
import cn.lary.pkg.srs.service.ProvinceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {
    private final SrsConfig srsConfig;
    private final HashMap<String,String> nearbyProvince = new HashMap<>();
    private final HashMap<String ,Integer[]> provinces = new HashMap<>();
    private final List<String> existsProvince = new ArrayList<>();

    @Override
    public boolean exist(String province) {
        boolean res = existsProvince.contains(province);
        if(res) return true;
        //do not contain direct province server
        return nearbyProvince.get(province) != null;
    }

    @Override
    public String getNearbyProvince(String province) {
        return nearbyProvince.get(province);
    }
    @PostConstruct
    public void init() {
        provinces.put("beijing",new Integer[]{116,40});
        provinces.put("shanghai",new Integer[]{122,31});
        provinces.put("tianjin",new Integer[]{117,39});
        provinces.put("chongqing",new Integer[]{107,40});
        provinces.put("shenzhen",new Integer[]{122,33});
        provinces.put("shenzhen",new Integer[]{122,34});
        provinces.put("shenzhen",new Integer[]{122,35});
        provinces.put("shenzhen",new Integer[]{122,36});
        provinces.put("shenzhen",new Integer[]{122,37});
        provinces.put("shenzhen",new Integer[]{122,38});
        provinces.put("shenzhen",new Integer[]{122,39});
        provinces.put("shenzhen",new Integer[]{122,40});
        provinces.put("shenzhen",new Integer[]{122,41});
        provinces.put("shenzhen",new Integer[]{122,41});
        provinces.put("shenzhen",new Integer[]{122,41});
        provinces.put("shenzhen",new Integer[]{122,41});
        provinces.put("shenzhen",new Integer[]{122,41});
        provinces.put("shenzhen",new Integer[]{122,41});

        // build exists provinces
        existsProvince.addAll(srsConfig.getEdgeServerProvinces());
        if(!existsProvince.isEmpty()) {
            log.error("no available nearby province,please check yml config");
        }
        // build nearby province
        provinces.forEach((k, v) -> {
            int tmp = Integer.MAX_VALUE;
            existsProvince.forEach(p -> {
                if (getTwoProvinceDiff(k,p) < tmp) {
                    // update the most nearby province
                    nearbyProvince.put(k,p);
                }
            });
        });
        // do what
    }
    private int getTwoProvinceDiff(String origin,String existsProvince) {
        Integer[] originArgs = provinces.get(origin);
        Integer[] existsArgs = provinces.get(existsProvince);
        int longitudeDiff = Math.abs(originArgs[0] - existsArgs[0]);
        int latitudeDiff = Math.abs(originArgs[1] - existsArgs[1]);
        return longitudeDiff + latitudeDiff;
    }
}
