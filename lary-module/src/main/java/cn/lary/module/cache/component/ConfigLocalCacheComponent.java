package cn.lary.module.cache.component;



public interface ConfigLocalCacheComponent {

    String  LARY_CONFIG = "lary:config:";


    void  changeRegister(boolean data);


    boolean getRegisterSwitch();


}
