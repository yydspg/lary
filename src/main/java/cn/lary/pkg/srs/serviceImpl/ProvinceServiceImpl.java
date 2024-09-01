package cn.lary.pkg.srs.serviceImpl;

import cn.lary.pkg.srs.config.SrsConfig;
import cn.lary.pkg.srs.service.ProvinceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {
    private final SrsConfig srsConfig;
    private final HashMap<String,String[]> nearbyProvince = new HashMap<>();
    private final HashMap<String ,Integer[]> provinces = new HashMap<>();
    private final List<String> existsProvince = new ArrayList<>();
    @Override
    public boolean exist(String province) {
        return false;
    }

    @Override
    public String getNearbyProvince(String province) {
        return "";
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


    }
}
