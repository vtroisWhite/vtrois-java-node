DROP TABLE IF EXISTS mapper_test_table;
CREATE TABLE `mapper_test_table`
(
    `table_id`    int(10) NOT NULL AUTO_INCREMENT,
    `uuid`        varchar(100)       DEFAULT NULL,
    `name`        varchar(100)       DEFAULT NULL,
    `counter`     int(10) DEFAULT '1' COMMENT '计数器',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS mapper_generate_test;
CREATE TABLE `mapper_generate_test`
(
    `table_id`    int(10) NOT NULL AUTO_INCREMENT,
    `uuid`        varchar(100)       DEFAULT NULL,
    `name`        varchar(100)       DEFAULT NULL,
    `counter`     int(10) DEFAULT '1' COMMENT '计数器',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;