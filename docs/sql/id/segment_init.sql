DROP TABLE IF EXISTS `lary_core`;

CREATE TABLE `lary_core` (
    `tag` varchar(128)  NOT NULL DEFAULT '',
    `max_id` bigint(20) NOT NULL DEFAULT '1',
    `step` int(11) NOT NULL,
    `description` varchar(256)  DEFAULT NULL,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`tag`)
) ENGINE=InnoDB;