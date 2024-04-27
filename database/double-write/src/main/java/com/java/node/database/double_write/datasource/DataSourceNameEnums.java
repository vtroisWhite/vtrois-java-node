package com.java.node.database.double_write.datasource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DataSourceNameEnums {

    DB_1("db1"),
    DB_2("db2");

    @Getter
    private final String name;

}
