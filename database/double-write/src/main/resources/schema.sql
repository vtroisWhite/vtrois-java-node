DROP TABLE IF EXISTS test_table_1;

CREATE TABLE `test_table_1`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL,
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `int_1`       int(11) DEFAULT NULL,
    `long_1`      bigint(20) DEFAULT NULL,
    `bit_1`       bit(64)               DEFAULT NULL,
    `date_1`      date                  DEFAULT NULL,
    `date_time_1` datetime              DEFAULT NULL,
    `decimal_1`   decimal(8, 4)         DEFAULT NULL,
    `text_1`      text,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;