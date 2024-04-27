package com.java.node.database.double_write.doubleWrite;

import com.java.node.database.double_write.datasource.DataSourceNameEnums;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.sql.DataSource;
import java.util.List;

/**
 * @Description
 */
@Data
@AllArgsConstructor
public class DoubleWriteParam {

    private DataSourceNameEnums targetDbName;

    private DataSource targetDataSource;

    private List<String> executorSql;

    private boolean transactionSuspend;

    public DoubleWriteParam(DataSourceNameEnums targetDbName, DataSource targetDataSource, List<String> executorSql) {
        this(targetDbName, targetDataSource, executorSql, false);
    }
}
