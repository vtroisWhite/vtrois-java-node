package com.java.node.database.mybatis_plus_v3x;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.type.ITypeConvertHandler;
import com.baomidou.mybatisplus.generator.type.TypeRegistry;
import com.java.node.database.mybatis_plus_v3x.config.mp.MyBaseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;


/**
 * @Description mysql的mapper文件生成类
 *
 * <a href="https://baomidou.com/pages/981406/#%E6%95%B0%E6%8D%AE%E5%BA%93%E9%85%8D%E7%BD%AE-datasourceconfig">文档</a>
 */
public class MySQLMapperGeneratorTest {

    /**
     * 要生成的表
     */
    private final static String[] TABLE_LIST = {
            "mapper_test_table",
    };
    /**
     * 项目的包路径
     */
    private final static String PROJECT_PACKAGE = "com.java.node.database.mybatis_plus_v3x";
    /**
     * 实体类的包路径
     */
    private final static String ENTITY_PACKAGE = "entity";
    /**
     * mapper接口的包路径
     */
    private final static String MAPPER_PACKAGE = "mapper";
    /**
     * mapper.xml的包路径，需要确保其在 /src/main/resources目录下
     */
    private final static String MAPPER_XML_PACKAGE = "mappers";


    @Test
    public void generator() throws IOException {
        System.out.println("项目根目录:" + getProjectRootPath());
        Properties properties = new Properties();
        InputStream in = new ClassPathResource("application.properties").getInputStream();
        // 使用properties对象加载输入流
        properties.load(in);

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig.Builder()
                //保存的文件根目录
                .outputDir(this.getProjectRootPath() + "/src/main/java")
                //不打开文件管理器
                .disableOpenDir()
                //作者
                .author("mybatis-plus-generator")
                //时间
                .commentDate("yyyy-MM-dd")
                .build();

        //数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(
                properties.getProperty("spring.datasource.url"),
                properties.getProperty("spring.datasource.username"),
                properties.getProperty("spring.datasource.password")
        )
                .dbQuery(new MySqlQuery())
                .schema("mybatis-plus")
                .typeConvertHandler(new ITypeConvertHandler() {
                    @Override
                    public IColumnType convert(GlobalConfig globalConfig, TypeRegistry typeRegistry, TableField.MetaInfo metaInfo) {
                        String fieldType = metaInfo.getJdbcType().name();
                        return new ITypeConvert() {
                            @Override
                            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                                //转换数据库字段映射的java字段的类型
                                if ("timestamp".equalsIgnoreCase(fieldType) || "datetime".equalsIgnoreCase(fieldType) || "date".equalsIgnoreCase(fieldType)) {
                                    return DbColumnType.DATE;
                                }
                                if ("tinyint".equalsIgnoreCase(fieldType)) {
                                    return DbColumnType.INTEGER;
                                }
                                return new MySqlTypeConvert().processTypeConvert(globalConfig, fieldType);
                            }
                        }.processTypeConvert(globalConfig, fieldType);
                    }
                })
                .build();

        //生成的文件的包路径配置
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent(PROJECT_PACKAGE)
                .entity(ENTITY_PACKAGE)
                .mapper(MAPPER_PACKAGE)
                //因为通过TemplateConfig模板取消了service、serviceImpl、controller的自动生成，所以这里的配置无效
                .service("service")
                .serviceImpl("service.impl")
                .controller("controller")
                //特别设置下xml的路径
                .pathInfo(Collections.singletonMap(OutputFile.xml, getProjectRootPath() + "/src/main/resources/" + MAPPER_XML_PACKAGE))    //配置 **Mapper.xml 路径信息：项目的 resources 目录的 Mapper 目录下
                .build();

        //模板配置
        TemplateConfig templateConfig = new TemplateConfig.Builder()
                //将service、serviceImpl、controller模板配置为空，从而不生成这三个文件
//                .service("")
//                .serviceImpl("")
                .controller("")
                .build();

        //生成的模板内容的策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                //------------基本配置--------------
                .enableCapitalMode()
                .enableSkipView()
                .disableSqlFilter()
                .addInclude(TABLE_LIST)
                //------------Entity配置--------------
                .entityBuilder()
                .enableFileOverride()
                //开启链式模型
                .enableChainModel()
                //禁用生成 serialVersionUID
                .disableSerialVersionUID()
                //开启 lombok 模型
                .enableLombok()
                //开启生成实体时生成字段注解
                .enableTableFieldAnnotation()
                //------------Mapper配置--------------
                .mapperBuilder()
                .enableFileOverride()
                //指定为我自己重新实现的mapper，因为加了几个自定义的sql，如果使用官方的BaseMapper则不配置即可
                .superClass(MyBaseMapper.class)
                .enableMapperAnnotation()
                .enableBaseResultMap()
                .enableBaseColumnList()
                .formatMapperFileName("%sMapper")
                .formatXmlFileName("%sMapper")
                //------------Controller配置--------------
                .controllerBuilder()
                .enableFileOverride()
                //------------Service配置--------------
                .serviceBuilder()
                .enableFileOverride()
                .formatServiceFileName("%sService")
                .build();

        //开始执行
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);
        generator.strategy(strategyConfig);
        generator.packageInfo(packageConfig);
        generator.global(globalConfig);
        generator.template(templateConfig);
        generator.execute();
    }

    /**
     * 获取项目的根目录
     *
     * @return
     */
    private String getProjectRootPath() {
        //获取文件目录
        String filePath = MySQLMapperGeneratorTest.class.getClassLoader().getResource(".").getPath();
        //处理掉目录后缀：/target/test-classes/
        filePath = filePath.substring(0, filePath.lastIndexOf("/target"));
        if (System.getProperty("os.name").toLowerCase().contains("windows") && filePath.startsWith("/")) {
            //windows不知道为什么有个/前缀，额外处理下
            filePath = filePath.substring(1);
        }
        return filePath;
    }
}
