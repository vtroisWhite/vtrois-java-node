package com.java.node.databse.tk_mybatis_v4x;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.java.node.databse.tk_mybatis_v4x.entity.MapperTestTable;
import com.java.node.databse.tk_mybatis_v4x.mapper.MapperTestTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class TkMybatisV4xApplicationTests {

    @Autowired
    private MapperTestTableMapper testTableMapper;

    @Test
    @DisplayName("增")
    public void create() {
        MapperTestTable table1 = new MapperTestTable();
        table1.setUuid(UUID.randomUUID().toString());
        table1.setName("tk-新增");

        //--------------------单条新增-insertSelective---------------------------
        //insertSelective，只保存不为null的列，从而实现可以使用数据库默认值的目的
        //执行sql：
        // INSERT INTO `mapper_test_table` ( `table_id`,`uuid`,`name` ) VALUES( ?,?,? )
        testTableMapper.insertSelective(table1.setTableId(null));
        log.info("insertSelective，执行结果：{}", JSON.toJSONString(testTableMapper.selectByPrimaryKey(table1.getTableId())));

        //--------------------单条新增-insert---------------------------
        //insert，保存所有列，因此除了自增的id主键，其余字段无法使用数据库默认值
        //执行sql：
        // INSERT INTO `mapper_test_table` ( `table_id`,`uuid`,`name`,`counter` ) VALUES( ?,?,?,? )
        testTableMapper.insert(table1.setTableId(null));
        log.info("insert，执行结果：{}", JSON.toJSONString(testTableMapper.selectByPrimaryKey(table1.getTableId())));
        MapperTestTable table2 = new MapperTestTable();
        String uuid = UUID.randomUUID().toString();
        table1.setUuid(uuid);
        table1.setName("tk-批量新增");
        List<MapperTestTable> list = Arrays.asList(table2, table2);

        //--------------------多条新增-insertList---------------------------
        //insertList，批量新增，无法使用数据库默认值，但实体的insertable=false属性可以生效
        //执行sql：
        // INSERT INTO `mapper_test_table` ( `uuid`,`name`,`counter` ) VALUES ( ?,?,? ) , ( ?,?,? )
        testTableMapper.insertList(list);
    }

    @Test
    @DisplayName("删")
    public void delete() {
        //--------------------单个主键删除,deleteByPrimaryKey---------------------------
        //sql:
        //DELETE FROM `mapper_test_table` WHERE `table_id` = ?
        testTableMapper.deleteByPrimaryKey(1);

        //--------------------多个主键删除,deleteByIds---------------------------
        //sql:
        //DELETE FROM `mapper_test_table` where `table_id` in (1,2,3)
        testTableMapper.deleteByIds("1,2,3");

        //--------------------条件删除,delete---------------------------
        //sql:
        //DELETE FROM `mapper_test_table` WHERE `uuid` = ?
        testTableMapper.delete(new MapperTestTable().setUuid(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("改")
    public void update() {
        MapperTestTable table = new MapperTestTable();
        table.setTableId(4);
        table.setName("tk-编辑");

        //--------------------根据主键更新,只更新非null的值，updateByPrimaryKeySelective---------------------------
        //sql:
        //UPDATE `mapper_test_table` SET `name` = ? WHERE `table_id` = ?
        testTableMapper.updateByPrimaryKeySelective(table);
        log.info("updateByPrimaryKeySelective，执行结果：{}", JSON.toJSONString(testTableMapper.selectByPrimaryKey(table.getTableId())));

        //--------------------根据主键更新,null值的字段也会更新为null,updateByPrimaryKey---------------------------
        //sql:
        //UPDATE `mapper_test_table` SET `uuid` = ?,`name` = ?,`counter` = ? WHERE `table_id` = ?
        testTableMapper.updateByPrimaryKey(table);
        log.info("updateByPrimaryKey，执行结果：{}", JSON.toJSONString(testTableMapper.selectByPrimaryKey(table.getTableId())));
    }

    @Test
    @DisplayName("查")
    public void read() {
        //--------------------主键查询单条,selectByPrimaryKey---------------------------
        //sql:
        //SELECT `table_id`,`uuid`,`name`,`counter`,`create_time`,`update_time` FROM `mapper_test_table` WHERE `table_id` = ?
        MapperTestTable table = testTableMapper.selectByPrimaryKey(4);

        //--------------------查询所有数据,selectAll---------------------------
        //sql:
        //SELECT `table_id`,`uuid`,`name`,`counter`,`create_time`,`update_time` FROM `mapper_test_table`
        List<MapperTestTable> list = testTableMapper.selectAll();

        //--------------------根据指定属性查找值,select---------------------------
        //sql:
        //SELECT `table_id`,`uuid`,`name`,`counter`,`create_time`,`update_time` FROM `mapper_test_table` WHERE `uuid` = ?
        testTableMapper.select(new MapperTestTable().setUuid(UUID.randomUUID().toString()));

        //--------------------根据指定属性查找值，如果返回多条会报错，只能返回单条或空，selectOne---------------------------
        //sql:
        //SELECT `table_id`,`uuid`,`name`,`counter`,`create_time`,`update_time` FROM `mapper_test_table` WHERE `uuid` = ?
        testTableMapper.selectOne(new MapperTestTable().setUuid(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("example条件")
    public void example() {
        Example example = new Example(MapperTestTable.class);
        //排除那些字段不查出来
        //example.excludeProperties("updateTime");
        //设定查出哪些字段
        example.selectProperties("tableId", "name", "createTime");
        //倒序查找
        example.orderBy("tableId").desc();
        Example.Criteria criteria = example.createCriteria();
        //设置条件：counter>0
        criteria.andGreaterThan("counter", 0);
        //sql：SELECT `table_id` , `name` , `create_time` FROM `mapper_test_table` WHERE ( ( `counter` > ? ) ) order by `table_id` DESC
        testTableMapper.selectByExample(example);
        //限制查询条件，offset：0，limit：1，代表从下标0的数据开始，获取前1条，因此获取的是第1条数据
        //但这个并没有直接把limit拼接在sql上，而是查出数据后，再截取数组返回回来，所以实际执行的sql和selectByExample一样
        List<MapperTestTable> list = testTableMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 1));
        log.info(JSON.toJSONString(list));

        //example第二种构造方法
        Example example2 = Example.builder(MapperTestTable.class)
                .select("tableId", "name", "createTime")
                .where(Sqls.custom().andGreaterThan("counter", 0))
                .orderByDesc("tableId")
                .build();
        //sql：SELECT `table_id` , `name` , `create_time` FROM `mapper_test_table` WHERE ( ( `counter` > ? ) ) order by `table_id` Desc
        testTableMapper.selectByExample(example2);

        //以上展示了example的写法，适用于select、update、delete中
        //但这种写法是动态生成sql，效率还是个小问题，更重要的是这种写法很难一眼看出sql结构，不利于维护，只建议个人项目使用这种写法。👎
    }

    @Test
    @DisplayName("分页查询")
    public void pager() {
        MapperTestTable queryParam = new MapperTestTable().setCounter(1);

        //使用RowBounds分页  但这个的实现是查出所有的数据，再截取数据，并不推荐使用👎
        List<MapperTestTable> list = testTableMapper.selectByRowBounds(queryParam, new RowBounds(1, 1));
        //SELECT `table_id`,`uuid`,`name`,`counter`,`create_time`,`update_time` FROM `mapper_test_table` WHERE `counter` = ?
        log.info("RowBounds分页结果：{}", JSON.toJSONString(list));

        //selectCount，获取数据条数
        //sql:SELECT COUNT(`table_id`) FROM `mapper_test_table` WHERE `counter` = ?
        int i = testTableMapper.selectCount(queryParam);
        log.info("selectCount条数结果：{}", i);

        //PageHelper插件分页，更合理的先查出总条数，再分页查询，并且会把分页数据处理成对象，方便前端处理，推荐使用👍
        //sql: 依次执行两个sql：
        //SELECT count(0) FROM `mapper_test_table` WHERE `counter` = ?
        //SELECT `table_id`,`uuid`,`name`,`counter`,`create_time`,`update_time` FROM `mapper_test_table` WHERE `counter` = ? limit ?,?
        PageHelper.startPage(1, 3);
        List<MapperTestTable> pageHelperList = testTableMapper.select(queryParam);
        PageInfo pageInfo = new PageInfo(pageHelperList);
        log.info("PageHelper分页结果：{}", JSON.toJSONString(pageInfo, SerializerFeature.PrettyFormat));
    }
}
