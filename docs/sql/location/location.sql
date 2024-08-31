DROP TABLE IF EXISTS `region`;
CREATE TABLE `region`  (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `parent_id` bigint NULL DEFAULT NULL COMMENT '父ID',
                              `ad_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '区域编码',
                              `center` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '区域中心点经纬度',
                              `city_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '城市代码',
                              `level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '行政区划级别',
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
                              `order_num` int NULL DEFAULT NULL COMMENT '排序',
                              `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '行政地区路径',
                              PRIMARY KEY (`id`) USING BTREE,
                              is_delete bool not null default false,
                              create_by varchar(40) ,
                              update_by varchar(40) ,
                              create_at timestamp,
                              update_at timestamp,
                              INDEX `parent_id`(`parent_id` ASC) USING BTREE COMMENT '父id'
) ENGINE = InnoDB;