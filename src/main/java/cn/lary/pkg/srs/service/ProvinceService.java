package cn.lary.pkg.srs.service;

public interface ProvinceService {

    public boolean exist(String province);
    String getNearbyProvince(String province);
}
