package com.java.node.database.multi_datasource;

import com.java.node.database.multi_datasource.mapper.db1.DB1TableTestMapper;
import com.java.node.database.multi_datasource.mapper.db2.DB2TableTestMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class MultiDatasourceApplicationTests {

    @Autowired
    private DB1TableTestMapper db1TableTestMapper;
    @Autowired
    private DB2TableTestMapper db2TableTestMapper;

    @Test
    @DisplayName("多数据源测试")
    public void testMultiDataSource() {
        String db1Vale = db1TableTestMapper.queryOne();
        String db2Vale = db2TableTestMapper.queryOne();
        System.out.println(db1Vale);
        System.out.println(db2Vale);
        assertNotEquals(db1Vale, db2Vale);
    }
}
