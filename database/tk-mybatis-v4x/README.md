TK-Mybatis (版本 4.x)
===========================
项目开源地址：https://github.com/abel533/Mapper

项目文档：https://github.com/abel533/Mapper/wiki

## 简介

TK-Mybatis是一个可以实现任意 MyBatis 通用方法的框架，项目提供了常规的增删改查操作以及Example 相关的单表操作。通用 Mapper
是为了解决 MyBatis 使用中 90% 的基本操作，使用它可以很方便的进行开发，可以节省开发人员大量的时间。

## 项目接入TK-Mybatis教程

1. pom.xml引入依赖

```xml
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>4.2.2</version>
        </dependency>
```

2. 修改@MapperScan

```
将项目中@MapperScan从 
org.mybatis.spring.annotation.MapperScan
改为
tk.mybatis.spring.annotation.MapperScan
```

3. Dao/Mapper的抽象类继承tk-mybatis接口

```java

//原版：
public interface MapperTestTableMapper{
     
}
//tk.mybatis版
public interface MapperTestTableMapper extends tk.mybatis.mapper.common.Mapper<MapperTestTable> ,
    //如果insertList的需求，可继承MySqlMapper实现
    tk.mybatis.mapper.common.special.InsertListMapper<MapperTestTable>,
    //如果selectByIds、deleteByIds的需求，可继承IdsMapper实现
    tk.mybatis.mapper.common.IdsMapper<MapperTestTable>{
     
}

```

4.
tk-mybatis的基本用法已经在此测试类中，可自行查看 [CodeTkMybatisApplicationTests.java](src%2Ftest%2Fjava%2Fcom%2Fexample%2Fstarcode%2Ftkmybatis%2FCodeTkMybatisApplicationTests.java)

## 引入代码生成插件

1. pom.xml引入插件

```xml

    <build>
        <plugins>
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.2</version>
                <!-- 配置文件及相关配置 -->
                <configuration>
                    <configurationFile>${basedir}/src/main/resources/tk-mybatis-generate-config.xml</configurationFile>
                    <overwrite>true</overwrite>
                    <verbose>true</verbose>
                </configuration>
                <dependencies>
                    <!-- 引入数据库连接依赖 -->
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>8.0.11</version>
                    </dependency>
                    <!-- 此处是基于tk.mybatis的mapper生成，所以引入依赖 -->
                    <dependency>
                        <groupId>tk.mybatis</groupId>
                        <artifactId>mapper-generator</artifactId>
                        <version>1.1.5</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```

2.
在resources目录下新建代码生成配置文件[tk-mybatis-generate-config.xml](src%2Fmain%2Fresources%2Ftk-mybatis-generate-config.xml)
3. IDEA的maven菜单中，应该在Plugins里已经出现了**mybatis-generator**选项，双击即可生成相关代码

## 使用总结

### 优点

- 配置简单方便，扩展功能精简够用，学习使用成本很低
- 支持select时通过实体作为参数进行查询，解决很多简单的单个字段条件sql的查询，并且可读性也很好

### 缺点

- example的条件查询十分不好用，需要用到字段魔法值，可读性很差，还不如去写xml的sql
- 代码生成工具比较难用，可配置的功能少，说明文档也简陋，比如不支持对数据库字段类型转为什么java类的自定义，比如tinyint会被转为Byte，如果想要转为Integer，还需要重写生成方法，比较麻烦

### 总结

因为足够的简单轻量，只是简单的解决update、insert等常用单表操作sql的编写，所以对项目的影响很小，而不支持的多表查询，使用xml进行sql维护，也可以保证可读性，整体而言是个值得使用的框架。

tk-mybatis在经历了2.x、3.x、4.x版本后，作者进行了重构，并把5.x的版本直接命名为了[MyBatis Mapper](https://mapper.mybatis.io/docs/1.getting-started.html#%E4%BB%8B%E7%BB%8D)
，这两个可以说是两个不同的框架，因此对于MyBatis Mapper之后在单独演示 