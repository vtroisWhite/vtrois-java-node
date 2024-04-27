package com.java.node.database.double_write;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.java.node.database.double_write.entity.TestTable1;
import com.java.node.database.double_write.mapper.TableTestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Description
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private TableTestMapper tableTestMapper;

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/doubleWrite/crud")
    public void crud() {
        Integer id = c();
        r(id);
        u(id);
        d(id);
    }

    /**
     * 新增数据，并测试事务嵌套子事务时，双写是否会有问题
     */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/doubleWrite/c")
    public Integer c() {
        TestController thisBean = context.getBean(this.getClass());
        TestTable1 entity = new TestTable1();
        entity.setName(getUUid() + "'\"?''\\\"'\"");
        entity.setDate1(DateUtil.beginOfDay(new Date()));
        entity.setDateTime1(new Date());
        entity.setInt1(new Random().nextInt());
        entity.setLong1(new Random().nextLong());
        entity.setBit1(new Random().nextLong());
        entity.setDecimal1(new BigDecimal("9.15"));
        entity.setText1(StrUtil.format("{},\n{},\n{},", getUUid(), getUUid(), getUUid()));
        tableTestMapper.insertSelective(entity);
        //开启子事务
        thisBean.doFuncOnNewTransactional(() -> {
            tableTestMapper.insertSelective(entity.setId(null));
            //子事务再开启子事务
            thisBean.doFuncOnNewTransactional(() -> tableTestMapper.insertSelective(entity.setId(null)));
        });
        //相同事务
        thisBean.doFuncOnSameTransactional(() -> tableTestMapper.insertSelective(entity.setId(null)));
        tableTestMapper.insertOne(entity.setId(null));
        log.info("新增成功:{}", entity);
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void doFuncOnSameTransactional(Runnable runnable) {
        runnable.run();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void doFuncOnNewTransactional(Runnable runnable) {
        runnable.run();
    }

    private String getUUid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 批量新增时，sql双写测试
     */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/doubleWrite/c_list")
    public void c_list() {
        TestTable1 t1 = new TestTable1().setName(getUUid()).setInt1(1);
        TestTable1 t2 = new TestTable1().setName(getUUid());
        List<TestTable1> testTable1s = Arrays.asList(t1, t2);
        //新增list，自动的id也能自动的设置回来
        tableTestMapper.insertMultiple(testTable1s);
        tableTestMapper.insertMultipleNoId(testTable1s);
        log.info("新增列表:{}", testTable1s);
    }

    /**
     * 查询数据，不会走到拦截器
     */
    @GetMapping("/doubleWrite/r")
    @Transactional(rollbackFor = Exception.class)
    public void r(Integer id) {
        TestTable1 data = tableTestMapper.selectByPrimaryKey(id);
        log.info("查询成功:{}", data);
    }

    /**
     * 修改数据，进入拦截器双写
     */
    @GetMapping("/doubleWrite/u")
    public void u(Integer id) {
        TestTable1 data = tableTestMapper.selectByPrimaryKey(id);
        data.setName(data.getName() + "_update");
        data.setUpdateTime(null);
        data.setDate1(null);
        int i = tableTestMapper.updateByPrimaryKeySelective(data);
        log.info("修改成功:{}", i);
    }

    /**
     * 删除数据，进入拦截器双写
     */
    @GetMapping("/doubleWrite/d")
    public void d(Integer id) {
        int i = tableTestMapper.deleteByPrimaryKey(id);
        log.info("删除成功:{}", i);
    }
}
