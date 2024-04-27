package com.java.node.database.mybatis_plus_v3x;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.node.database.mybatis_plus_v3x.entity.MapperTestTable;
import com.java.node.database.mybatis_plus_v3x.mapper.MapperTestTableMapper;
import com.java.node.database.mybatis_plus_v3x.service.MapperTestTableService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class MyBatisPlusV3xApplicationTests {

    @Autowired
    private MapperTestTableService testTableService;
    @Autowired
    private MapperTestTableMapper testTableMapper;

    @Test
    @DisplayName("增")
    public void create() {
        MapperTestTable table1 = new MapperTestTable();
        table1.setUuid(UUID.randomUUID().toString());
        table1.setName("mp-新增");
        //--------------------单条新增-save，同tk的insertSelective---------------------------
        //insertSelective，只保存不为null的列，从而实现可以使用数据库默认值的目的
        //执行sql：
        //  INSERT INTO mapper_test_table ( uuid, name ) VALUES ( ?, ? )
        testTableService.save(table1);

        List<MapperTestTable> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MapperTestTable table2 = new MapperTestTable();
            table2.setUuid(UUID.randomUUID().toString());
            table2.setName("mp-批量新增-" + i);
            if (i == 1) {
                //table2.setCounter(2);
            }
            list.add(table2);
        }

        //--------------------批量新增-saveBatch---------------------------
        //如果每条数据的，每列值都一样有值，则按照设定的每批数量新增
        //但如果每条数据每列有的有值，有的没值，比如这个的第2条多个counter，就会被分拆为1、1、1三次新增
        //另外新增时和insertSelective一样，只新增有值的字段
        //sql：INSERT INTO mapper_test_table ( uuid, name ) VALUES ( ?, ? ) 因为定义了一次插入2条，所以执行了两次，第一次插入2条，第二次插入1条
        testTableService.saveBatch(list, 2);

        //--------------------自定义sql,批量新增，但用所有的字段
        //这个是mybatis-plus提供的扩展，用于新增所有的字段，可以实现 tk的insert的效果
        /** {@link com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn */
        //执行sql：INSERT INTO mapper_test_table (uuid,name,counter) VALUES (?,?,?) , (?,?,?) , (?,?,?)
        testTableMapper.saveBatchAllColumn(list);

        //--------------------自定义sql,根据uuid查询数据
        //这是完全自定义的sql，根据官方的教程写的，实现位于：ListByUuid.java
        //sql： select table_id,uuid,name,counter,create_time,update_time from mapper_test_table where uuid=?
        List<MapperTestTable> uuidResult = testTableMapper.listByUuid("8e5555a6-952f-4876-833f-c8669c53dede");
        log.info("uuid查询结果：" + JSON.toJSONString(uuidResult));
    }

    @Test
    @DisplayName("删")
    public void delete() {
        //--------------------单个主键删除,removeById---------------------------
        //sql:
        //DELETE FROM mapper_test_table WHERE table_id=?
        testTableService.removeById(1);

        //--------------------多个主键删除,removeByIds---------------------------
        //sql:
        //DELETE FROM mapper_test_table WHERE table_id IN ( ? , ? , ? )
        testTableService.removeByIds(Arrays.asList(1, 2, 3));
    }

    @Test
    @DisplayName("改")
    public void update() {
        MapperTestTable table = new MapperTestTable();
        table.setTableId(28);
        table.setName("mp-编辑");

        //--------------------根据主键更新,只更新非null的值，updateById，等同于tk的updateByPrimaryKeySelective---------------------------
        //sql:
        //UPDATE mapper_test_table SET name=? WHERE table_id=?
        testTableService.updateById(table);
        log.info("updateById，执行结果：{}", JSON.toJSONString(testTableService.getById(table.getTableId())));

        //--------------------根据主键更新,null值的字段也会更新为null,等同于tk的updateByPrimaryKey---------------------------
        //另外这是一个扩展方法，2.x版本是有这个方法的，3.x就是自己实现这个扩展方法才能用了，搞不懂作者为什么要这么搞，很影响使用体验
        //sql:
        //UPDATE mapper_test_table SET uuid=?, name=?, counter=? WHERE table_id=?
        testTableMapper.updateAllColumnById(table);
        log.info("updateAllColumnById，执行结果：{}", JSON.toJSONString(testTableService.getById(table.getTableId())));
    }

    @Test
    @DisplayName("查")
    public void read() {
        //--------------------主键查询单条,getById,等同于selectByPrimaryKey---------------------------
        //sql:
        //SELECT table_id,uuid,name,counter,create_time,update_time FROM mapper_test_table WHERE table_id=?
        testTableService.getById(1);
        //--------------------查询所有数据,list---------------------------
        //sql:
        //SELECT table_id,uuid,name,counter,create_time,update_time FROM mapper_test_table
        testTableService.list();
    }

    @Test
    @DisplayName("Wrapper使用")
    public void wrapper() {
        String uuid = "8e5555a6-952f-4876-833f-c8669c53dede";
        //----------------------------------QueryWrapper------------------------------------
        //SELECT table_id,uuid,name,counter,create_time,update_time FROM mapper_test_table WHERE (uuid = ?) limit 1
        testTableService.getOne(new QueryWrapper<MapperTestTable>()
                .lambda()
                .eq(MapperTestTable::getUuid, uuid)
                .last("limit 1"));


        //SELECT table_id,name,create_time FROM mapper_test_table WHERE (uuid = ? AND counter >= ?)
        testTableService.list(new QueryWrapper<MapperTestTable>()
                .lambda()
                .select(MapperTestTable::getTableId, MapperTestTable::getName, MapperTestTable::getCreateTime)
                .eq(MapperTestTable::getUuid, uuid)
                .ge(MapperTestTable::getCounter, 1));

        //----------------------------------UpdateWrapper------------------------------------
        // UPDATE mapper_test_table SET counter=? WHERE (table_id = ?)
        testTableService.update(new UpdateWrapper<MapperTestTable>()
                .lambda()
                .set(MapperTestTable::getCounter, null)
                .eq(MapperTestTable::getTableId, 1)
        );

    }

    @Test
    @DisplayName("分页查询")
    public void pager() {
        Page<MapperTestTable> pages = new Page<>(1, 5);
        MapperTestTable param = new MapperTestTable();

        //对xml的sql进行分页
        //sql：
        // SELECT COUNT(*) AS total FROM mapper_test_table
        // select table_id, uuid, name, counter, create_time, update_time from mapper_test_table LIMIT ?
        //先count查出条数，然后分页查出数据。
        IPage<MapperTestTable> result1 = testTableMapper.queryList(pages, param);
        printPageResult(result1);

        //使用自带的分页方法，但使用QueryWrapper生成查询sql
        //sql：
        //SELECT COUNT(*) AS total FROM mapper_test_table
        //SELECT table_id,uuid,name,counter,create_time,update_time FROM mapper_test_table LIMIT ?
        //与使用xml的sql查询并没有什么区别
        Page<MapperTestTable> result3 = testTableService.page(pages, new QueryWrapper<MapperTestTable>());
        printPageResult(result3);
    }

    private void printPageResult(IPage<MapperTestTable> result) {
        System.out.println("总条数 ------> " + result.getTotal());
        System.out.println("当前页数 ------> " + result.getCurrent());
        System.out.println("当前每页显示数 ------> " + result.getSize());
        System.out.println("查询结果 ------> " + JSON.toJSONString(result, SerializerFeature.PrettyFormat));
    }
}
