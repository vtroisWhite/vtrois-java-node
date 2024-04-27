package com.java.node.database.mybatis_plus_v3x.config.mp;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.java.node.database.mybatis_plus_v3x.config.mp.methods.ListByUuid;

import java.util.List;

/**
 * 自定义Sql注入
 */
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        //增加自定义方法
        methodList.add(new ListByUuid("listByUuid"));
        //官方内置的新增所有字段方法
        methodList.add(new InsertBatchSomeColumn("saveBatchAllColumn", i -> i.getInsertStrategy() != FieldStrategy.NEVER));
        //官方内置的修改所有字段方法
        methodList.add(new AlwaysUpdateSomeColumnById("updateAllColumnById", i -> i.getUpdateStrategy() != FieldStrategy.NEVER));
        return methodList;
    }
}
