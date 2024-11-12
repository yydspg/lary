-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255)COMMENT '创建者',
                             `create_at` datetime(6) COMMENT '创建时间',
                             `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255)COMMENT '更新者',
                             `update_at` datetime(6) COMMENT '更新时间',
                             `auth_message` varchar(255)COMMENT '审核信息',
                             `brand_id` varchar(255)COMMENT '品牌ID',
                             `buy_count` int NULL DEFAULT 0 COMMENT '购买数量',
                             `category_path` varchar(255)COMMENT '分类路径',
                             `comment_num` int COMMENT '评论数量',
                             `cost` decimal(10, 2) COMMENT '成本价格',
                             `goods_name` varchar(255)COMMENT '商品名称',
                             `goods_unit` varchar(255)COMMENT '计量单位',
                             `goods_video` varchar(255)COMMENT '商品视频',
                             `grade` decimal(10, 2) COMMENT '商品好评率',
                             `intro` text NULL COMMENT '商品详情',
                             `auth_flag` varchar(255)COMMENT '审核状态',
                             `market_enable` varchar(255)COMMENT '上架状态',
                             `mobile_intro` text NULL COMMENT '商品移动端详情',
                             `original` varchar(255)COMMENT '原图路径',
                             `price` decimal(10, 2) COMMENT '商品价格',
                             `quantity` int NULL DEFAULT 0 COMMENT '库存',
                             `recommend` bit(1) COMMENT '是否为推荐商品',
                             `sales_model` varchar(255)COMMENT '销售模式',
                             `self_operated` bit(1) COMMENT '是否自营',
                             `store_id` bigint COMMENT '店铺ID',
                             `store_name` varchar(255)COMMENT '店铺名称',
                             `selling_point` varchar(255)COMMENT '卖点',
                             `shop_category_path` varchar(255)COMMENT '店铺分类',
                             `small` varchar(255)COMMENT '小图路径',
                             `sn` varchar(30)COMMENT '商品编号',
                             `template_id` varchar(255)COMMENT '运费模板ID',
                             `thumbnail` varchar(255)COMMENT '缩略图路径',
                             `under_message` varchar(255)COMMENT '下架原因',
                             `weight` decimal(10, 2) COMMENT '重量',
                             `store_category_path` varchar(255)COMMENT '店铺分类路径',
                             `big` varchar(255)COMMENT '大图路径',
                             `params` text NULL,
                             `goods_type` varchar(255)NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `store_id`(`store_id` ASC) USING BTREE COMMENT '店铺id索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
-- Table structure for goods_collection
-- ----------------------------
DROP TABLE IF EXISTS `goods_collection`;
CREATE TABLE `goods_collection`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_at` datetime(6) COMMENT '创建时间',
                                        `member_id` varchar(255)COMMENT '会员id',
                                        `sku_id` varchar(255)COMMENT '商品id',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Table structure for goods_gallery
-- ----------------------------
DROP TABLE IF EXISTS `goods_gallery`;
CREATE TABLE `goods_gallery`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)COMMENT '创建者',
                                     `goods_id` bigint COMMENT '商品ID',
                                     `is_default` int COMMENT '是否是默认图片',
                                     `original` varchar(255)COMMENT '原图路径',
                                     `small` varchar(255)COMMENT '小图路径',
                                     `sort` int COMMENT '排序',
                                     `thumbnail` varchar(255)COMMENT '缩略图路径',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;-- ----------------------------
-- Table structure for goods_params
-- ----------------------------
DROP TABLE IF EXISTS `goods_params`;
CREATE TABLE `goods_params`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255)COMMENT '创建者',
                                    `create_at` datetime(6) COMMENT '创建时间',
                                    `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255)COMMENT '更新者',
                                    `update_at` datetime(6) COMMENT '更新时间',
                                    `goods_id` bigint COMMENT '商品ID',
                                    `param_id` varchar(255)COMMENT '参数ID',
                                    `param_name` varchar(255)COMMENT '参数名字',
                                    `param_value` varchar(100)COMMENT '参数值',
                                    `is_index` int NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Table structure for goods_sku
-- ----------------------------
DROP TABLE IF EXISTS `goods_sku`;
CREATE TABLE `goods_sku`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255)COMMENT '创建者',
                                 `create_at` datetime(6) COMMENT '创建时间',
                                 `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255)COMMENT '更新者',
                                 `update_at` datetime(6) COMMENT '更新时间',
                                 `auth_message` varchar(255)COMMENT '审核信息',
                                 `big` varchar(255)COMMENT '大图路径',
                                 `brand_id` varchar(255)COMMENT '品牌ID',
                                 `buy_count` int COMMENT '购买数量',
                                 `category_path` varchar(255)COMMENT '分类路径',
                                 `comment_num` int COMMENT '评价数量',
                                 `cost` decimal(10, 2) COMMENT '成本价格',
                                 `freight_payer` varchar(255)COMMENT '运费承担者',
                                 `freight_template_id` varchar(255)COMMENT '配送模版ID',
                                 `goods_id` bigint COMMENT '商品ID',
                                 `goods_name` varchar(255)COMMENT '商品名称',
                                 `goods_unit` varchar(255)COMMENT '计量单位',
                                 `goods_video` varchar(255)COMMENT '商品视频',
                                 `grade` decimal(10, 2) COMMENT '商品好评率',
                                 `intro` text NULL COMMENT '商品详情',
                                 `auth_flag` varchar(255)COMMENT '审核状态',
                                 `promotion_flag` bool COMMENT '是否是促销商品',
                                 `market_enable` varchar(255)COMMENT '上架状态',
                                 `mobile_intro` text NULL COMMENT '移动端商品详情',
                                 `original` varchar(255)COMMENT '原图路径',
                                 `price` decimal(10, 2) COMMENT '商品价格',
                                 `promotion_price` decimal(10, 2) COMMENT '促销价',
                                 `quantity` int COMMENT '库存',
                                 `recommend` bit(1) NOT NULL COMMENT '是否为推荐商品',
                                 `sales_model` varchar(255)COMMENT '销售模式',
                                 `self_operated` bit(1) COMMENT '是否自营',
                                 `store_id` varchar(255)COMMENT '店铺ID',
                                 `store_name` varchar(255)COMMENT '店铺名称',
                                 `selling_point` varchar(255)COMMENT '卖点',
                                 `small` varchar(255)COMMENT '小图路径',
                                 `sn` varchar(30)COMMENT '商品编号',
                                 `specs` text NULL COMMENT '规格信息json',
                                 `template_id` varchar(255)COMMENT '运费模板id',
                                 `thumbnail` varchar(255)COMMENT '缩略图路径',
                                 `under_message` varchar(255)COMMENT '下架原因',
                                 `view_count` int COMMENT '浏览数量',
                                 `weight` decimal(10, 2) COMMENT '重量',
                                 `simple_specs` avrchar(255)COMMENT '规格信息',
                                 `store_category_path` varchar(255)COMMENT '店铺分类路径',
                                 `goods_type` varchar(255)NULL DEFAULT NULL,
                                 `alert_quanti3ty` int COMMENT '预警库存',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of goods_sku
-- ----------------------------
INSERT INTO `goods_sku` VALUES (1376538660840669184, '15810610731', '2021-03-29 09:16:06.253000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943,1348576427264204943', NULL, 4100.00, 'STORE', '1376425599173656576', '1376538660148609024', 'OPPO Ace 黑色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 3800.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '双模5G 40W无线闪充 65W超级闪充 高通骁龙865 185g 90Hz电竞屏 180Hz采样率 拍照游戏智能手机 ', NULL, '166666', '{\"images\":[{\"showProgress\":false,\"uid\":1617027268719,\"size\":179492,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617027245031},\"percentage\":100,\"name\":\"1111111111111.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617027268720,\"size\":175246,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617027255460},\"percentage\":100,\"name\":\"2222222.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617027268721,\"size\":179527,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617027265937},\"percentage\":100,\"name\":\"3333333.jpg\",\"status\":\"finished\"}],\"颜色\":\"黑色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 黑色', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376538661029412864, '15810610731', '2021-03-29 09:16:06.298000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943,1348576427264204943', NULL, 4500.00, 'STORE', '1376425599173656576', '1376538660148609024', 'OPPO Ace 幻影紫', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 3900.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '双模5G 40W无线闪充 65W超级闪充 高通骁龙865 185g 90Hz电竞屏 180Hz采样率 拍照游戏智能手机 ', NULL, '1555555', '{\"images\":[{\"showProgress\":false,\"uid\":1617027302596,\"size\":414230,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617027287063},\"percentage\":100,\"name\":\"555555.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617027302597,\"size\":63930,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617027299820},\"percentage\":100,\"name\":\"44444.jpg\",\"status\":\"finished\"}],\"颜色\":\"幻影紫\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 幻影紫', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376551376737271808, '15810610731', '2021-03-29 10:06:37.959000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943', NULL, 3799.00, 'STORE', '1376425599173656576', '1376551376087154688', '一加 OnePlus 9 紫色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 4000.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '8GB+128GB 5G手机 一加｜哈苏 手机影像系统 6.55 英寸 5000万主摄级超广角 120Hz柔性直屏 拍照手机 游戏手机', NULL, '38472', '{\"images\":[{\"showProgress\":false,\"uid\":1617030332761,\"size\":18264,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617030308874},\"percentage\":100,\"name\":\"紫色2222.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617030332762,\"size\":23531,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617030321821},\"percentage\":100,\"name\":\"紫色1111.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617030332763,\"size\":26334,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617030329973},\"percentage\":100,\"name\":\"紫色3333.jpg\",\"status\":\"finished\"}],\"颜色\":\"紫色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 紫色', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376551376959569920, '15810610731', '2021-03-29 10:06:38.012000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943', NULL, 3699.00, 'STORE', '1376425599173656576', '1376551376087154688', '一加 OnePlus 9 黑色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 4100.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '8GB+128GB 5G手机 一加｜哈苏 手机影像系统 6.55 英寸 5000万主摄级超广角 120Hz柔性直屏 拍照手机 游戏手机', NULL, '8214817', '{\"images\":[{\"showProgress\":false,\"uid\":1617030372686,\"size\":12318,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617030349674},\"percentage\":100,\"name\":\"一家22222.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617030372687,\"size\":27386,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617030360992},\"percentage\":100,\"name\":\"一家33333.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617030372688,\"size\":26587,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617030369884},\"percentage\":100,\"name\":\"一家1111111.jpg\",\"status\":\"finished\"}],\"颜色\":\"黑色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 黑色', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376567227649622016, '15810610731', '2021-03-29 11:09:37.111000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943', NULL, 3999.00, 'STORE', '1376425599173656576', '1376567227083390976', '华为(HUAWEI) 华为nova 8 黑色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 4999.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '5G 全网通版 8GB+128GB 亮黑色 麒麟985 6400万四摄 移动联通电信5G拍照游戏智能手机 双卡双待 华为nova8手机5g', NULL, '32742373472', '{\"images\":[{\"showProgress\":false,\"uid\":1617034117304,\"size\":560804,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617034109520},\"percentage\":100,\"name\":\"111111111111111111111111.png\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617034117305,\"size\":276965,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617034114577},\"percentage\":100,\"name\":\"22222222222222.png\",\"status\":\"finished\"}],\"颜色\":\"黑色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 黑色', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376567227838365696, '15810610731', '2021-03-29 11:09:37.156000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943', NULL, 2888.00, 'STORE', '1376425599173656576', '1376567227083390976', '华为(HUAWEI) 华为nova 8 绿色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 3888.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '5G 全网通版 8GB+128GB 亮黑色 麒麟985 6400万四摄 移动联通电信5G拍照游戏智能手机 双卡双待 华为nova8手机5g', NULL, '5437578347535', '{\"images\":[{\"showProgress\":false,\"uid\":1617034136952,\"size\":562083,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617034127292},\"percentage\":100,\"name\":\"333333333333333333.png\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617034136953,\"size\":277556,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617034134230},\"percentage\":100,\"name\":\"444444444444444444444.png\",\"status\":\"finished\"}],\"颜色\":\"绿色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 绿色', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376582977684766720, '15810610731', '2021-03-29 12:12:12.212000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943', NULL, 1000.00, 'STORE', '1376425599173656576', '1376582977630240768', '灵眸运动相机 双彩屏 超强增稳 超清画质 裸机防水 绿色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 1690.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '麒麟810芯片 OLED屏幕指纹 3D超薄机身 4800万高感光夜拍三摄 6.3英寸OLED珍珠屏全面屏手机', NULL, '219301', '{\"images\":[{\"showProgress\":false,\"uid\":1617037890135,\"size\":262754,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617037880071},\"percentage\":100,\"name\":\"1111111111111111111111111111.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617037890136,\"size\":154823,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617037887431},\"percentage\":100,\"name\":\"22222222222222222222222.jpg\",\"status\":\"finished\"}],\"颜色\":\"绿色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 绿色', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376582977869316096, '15810610731', '2021-03-29 12:12:12.256000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204942,1348576427264204943', NULL, 1500.00, 'STORE', '1376425599173656576', '1376582977630240768', '灵眸运动相机 双彩屏 超强增稳 超清画质 裸机防水 黑色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 2000.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '麒麟810芯片 OLED屏幕指纹 3D超薄机身 4800万高感光夜拍三摄 6.3英寸OLED珍珠屏全面屏手机', NULL, '234244', '{\"images\":[{\"showProgress\":false,\"uid\":1617037921204,\"size\":239022,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617037909021},\"percentage\":100,\"name\":\"33333333333333333333333333.jpg\",\"status\":\"finished\"},{\"showProgress\":false,\"uid\":1617037921205,\"size\":149981,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617037918471},\"percentage\":100,\"name\":\"44444444444444444444.jpg\",\"status\":\"finished\"}],\"颜色\":\"黑色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 2.00, ' 黑色', NULL, 'PHYSICAL_GOODS', 0);
INSERT INTO `goods_sku` VALUES (1376716129480736768, '15810610731', '2021-03-29 21:01:18.074000', b'1', NULL, '1970-01-19 04:39:31.928000', NULL, NULL, '0', NULL, '1348576427264204941,1348576427264204981,1348576427264204982', NULL, 2999.00, 'STORE', '1376425599173656576', '1376716129283604480', '惠普(HP)星青春版 蓝色', '个', NULL, NULL, NULL, 'PASS', NULL, 'DOWN', NULL, NULL, 3999.00, NULL, 1000, b'0', 'RETAIL', NULL, '1376369067769724928', 'Lilishop自营店', '14s-dr2002TU 14英寸十一代学生网课办公轻薄本笔记本电脑(i5-1135G7 核芯显卡 16G内存 512SSD AI智能语音）', NULL, '859240', '{\"images\":[{\"showProgress\":false,\"uid\":1617069589605,\"size\":169768,\"response\":{\"code\":60000,\"message\":\"请先登录再访问此接口\",\"success\":false,\"timestamp\":1617069586114},\"percentage\":100,\"name\":\"111111111111111111111111.jpg\",\"status\":\"finished\"}],\"颜色\":\"蓝色\"}', NULL, 'null?x-oss-process=style/400X400', NULL, NULL, 5.00, ' 蓝色', NULL, 'PHYSICAL_GOODS', 0);
-- ----------------------------
-- Table structure for goods_unit
-- ----------------------------
DROP TABLE IF EXISTS `goods_unit`;
CREATE TABLE `goods_unit`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255)COMMENT '创建者',
                                  `create_at` datetime(6) COMMENT '创建时间',
                                  `delete_flag` bit(1) COMMENT '计量单位名称',
                                  `update_by` varchar(255)COMMENT '更新者',
                                  `update_at` datetime(6) COMMENT '更新时间',
                                  `name` varchar(255)COMMENT '删除标志 true/false 删除/未删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of goods_unit
-- ----------------------------
INSERT INTO `goods_unit` VALUES (1376371484724822016, 'admin', '2021-03-28 22:11:48.361000', b'0', 'admin', '2021-07-09 16:29:24.889000', '只');
INSERT INTO `goods_unit` VALUES (1376371500533153792, 'admin', '2021-03-28 22:11:52.130000', b'0', NULL, NULL, '件');
INSERT INTO `goods_unit` VALUES (1376371521315930112, 'admin', '2021-03-28 22:11:57.085000', b'0', NULL, NULL, '份');
INSERT INTO `goods_unit` VALUES (1376371538806177792, 'admin', '2021-03-28 22:12:01.255000', b'0', 'admin', '2021-09-22 15:18:18.907000', '克');

-- ----------------------------
-- Table structure for goods_words
-- ----------------------------
DROP TABLE IF EXISTS `goods_words`;
CREATE TABLE `goods_words`  (
                                   `id` bigint NOT NULL,
                                   `create_by` varchar(255)NULL DEFAULT NULL,
                                   `create_at` datetime(6) NULL DEFAULT NULL,
                                   `delete_flag` bit(1) NULL DEFAULT NULL,
                                   `update_by` varchar(255)NULL DEFAULT NULL,
                                   `update_at` datetime(6) NULL DEFAULT NULL,
                                   `abbreviate` varchar(255)NULL DEFAULT NULL,
                                   `sort` int NULL DEFAULT NULL,
                                   `category` varchar(255)NULL DEFAULT NULL,
                                   `whole_spell` varchar(255)NULL DEFAULT NULL,
                                   `words` varchar(255)NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for points_goods
-- ----------------------------
DROP TABLE IF EXISTS `points_goods`;
CREATE TABLE `points_goods`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                                    `create_at` datetime COMMENT '创建时间',
                                    `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '更新者',
                                    `update_at` datetime COMMENT '更新时间',
                                    `end_time` datetime COMMENT '活动结束时间',
                                    `promotion_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '活动名称',
                                    `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '店铺ID',
                                    `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '店铺名称',
                                    `start_time` datetime COMMENT '活动开始时间',
                                    `active_stock` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '活动库存数量',
                                    `points` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '兑换积分',
                                    `points_goods_category_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '积分商品分类编号',
                                    `points_goods_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类名称',
                                    `settlement_price` decimal(10, 2) COMMENT '结算价格',
                                    `sku_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '商品编号',
                                    `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                    `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                    `original_price` decimal(10, 2) COMMENT '原价',
                                    `thumbnail` varchar(255)  COMMENT '缩略图',
                                    `goods_id` varchar(255)  COMMENT '商品编号',
                                    `goods_name` varchar(255)  COMMENT '货品名称',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of points_goods
-- ----------------------------

-- ----------------------------
-- Table structure for points_goods_category
-- ----------------------------
DROP TABLE IF EXISTS `points_goods_category`;
CREATE TABLE `points_goods_category`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                                             `create_at` datetime COMMENT '创建时间',
                                             `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                             `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '更新者',
                                             `update_at` datetime COMMENT '更新时间',
                                             `level` int COMMENT '层级',
                                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类名称',
                                             `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '父ID',
                                             `sort_order` decimal(19, 2) COMMENT '排序值',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of points_goods_category
-- ----------------------------

-- ----------------------------
-- Table structure for promotion_goods
-- ----------------------------
DROP TABLE IF EXISTS `promotion_goods`;
CREATE TABLE `promotion_goods`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255)COMMENT '创建者',
                                       `create_at` datetime(6) COMMENT '创建时间',
                                       `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255)COMMENT '更新者',
                                       `update_at` datetime(6) COMMENT '更新时间',
                                       `end_time` datetime(6) COMMENT '活动结束时间',
                                       `goods_name` varchar(255)COMMENT '货品名称',
                                       `limit_num` int COMMENT '限购数量',
                                       `num` int COMMENT '卖出的商品数量',
                                       `price` decimal(10, 2) COMMENT '促销价格',
                                       `promotion_id` varchar(255)COMMENT '活动ID',
                                       `quantity` int COMMENT '促销库存',
                                       `promotion_type` varchar(255)COMMENT '促销工具类型',
                                       `store_id` varchar(255)COMMENT '店铺ID',
                                       `store_name` varchar(255)COMMENT '店铺名称',
                                       `sku_id` varchar(255)COMMENT '货品ID',
                                       `start_time` datetime(6) COMMENT '活动开始时间',
                                       `thumbnail` varchar(255)COMMENT '缩略图',
                                       `title` varchar(255)COMMENT '活动标题',
                                       `category_path` varchar(255)COMMENT '分类路径',
                                       `goods_type` varchar(255)NULL DEFAULT NULL,
                                       `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                       `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                       `original_price` decimal(10, 2) COMMENT '原价',
                                       `points` varchar(255)  COMMENT '兑换积分',
                                       `goods_id` varchar(255)  COMMENT '商品编号',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of promotion_goods
-- ----------------------------
