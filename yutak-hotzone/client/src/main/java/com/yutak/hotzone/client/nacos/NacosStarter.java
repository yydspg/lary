package com.yutak.hotzone.client.nacos;



public class NacosStarter {

//    private final static String GROUP = "yutak";
//    private final static String DATA_ID = "yutak-hotzone";
//    private static final Logger log = LoggerFactory.getLogger(NacosStarter.class);
//    private  static final Executor executor = new ThreadPoolExecutor(1,1,30,
//            TimeUnit.DAYS,new ArrayBlockingQueue<Runnable>(1024));
//
//    public static void start() {
//        String serverAddr = "127.0.0.1";
//        try{
//            Properties properties = new Properties();
//            System.setProperty("com.alibaba.nacos.client.naming", "WARN");
//            System.setProperty("com.alibaba.nacos.client.config.impl", "WARN");
//            System.setProperty(PropertyKeyConst.SERVER_ADDR, "127.0.0.1");
//            ConfigService configService = NacosFactory.createConfigService(properties);
//            String config = configService.getConfigAndSignListener(DATA_ID, GROUP, 100L, new Listener() {
//                @Override
//                public Executor getExecutor() {
//                    return executor;
//                }
//
//                @Override
//                public void receiveConfigInfo(String config) {
//                   connect(config);
//                }
//            });
//            connect(config);
//        } catch (NacosException e) {
//            log.error("yutak hotzone start error message:" + e.getMessage());
//        }
//    }
//    private static void connect(String config) {
//        JSONObject object = JSON.parseObject(config);
//        JSONArray server = object.getJSONArray("ip");
//        List<String> workers = server.stream().map(Object::toString).toList();
//        YutakNettyClient.getInstance().connect(workers);
//    }
}
