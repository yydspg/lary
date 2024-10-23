package cn.lary.starter;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.sql.Types;
import java.util.Arrays;


/*
 * @author paul 2024/1/31
 */

public class Codec{
    private static final String SERVICE_NAME = "group";

    //dataSource config
    private static final String DATA_SOURCE_USER_NAME  = "root";
    private static final String DATA_SOURCE_PASSWORD  = "12345678";
    //package config
    private static final String PARENT_PACK_NAME = "cn.lary.module";
    private static final String XML_PACK_NAME = "mapper";
    private static final String SERVICE_IMPL_PACK_NAME = "serviceImpl";
    private static final String ENTITY_PACK_NAME = "entity";
    private static final String DATA_SOURCE_URL = "jdbc:mysql://192.168.1.101:3306/lary";
    private static final String[] TABLE_NAMES = new String[]{
           "group_member",
    };
    public static void main(String[] args) {
        /* 配置数据源 **/
        FastAutoGenerator.create(new DataSourceConfig.Builder(DATA_SOURCE_URL, DATA_SOURCE_USER_NAME, DATA_SOURCE_PASSWORD))
                .globalConfig(builder -> {
                    /* 自定义作者 **/
                    builder.author("paul")
                            /* 开启springdoc,配置springdoc输出,原因是knife4j底层基于springdoc**/
//                            .enableSpringdoc()
                            /* 开启swagger,不能同时与springdoc开启,swagger优先级低于springdoc**/
                            /*.enableSwagger() **/
                            /* 关闭路径打开,默认开启,效果是本地打开文件夹 **/
                            .disableOpenDir()
                            /* 指定输出目录 **/
                            .outputDir("/home/paul/javaPro/out/generated");
                })
                /* 数据库配置 **/
                .dataSourceConfig(builder -> builder
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.TINYINT) {
                                //tinyint转换成Boolean
                                return DbColumnType.BYTE;
                            }
                            return typeRegistry.getColumnType(metaInfo);

                        }))
                /* 包路径配置 ,也就是主目录下各个模块生成的文件名 **/
                .packageConfig(builder -> {
                    /* 父包名 **/
                    builder.parent(PARENT_PACK_NAME)
                            /* xml 文件路径 **/
                            .xml(XML_PACK_NAME)
                            /* 模块名,实际路径是PARENT_PACK_NAME+SERVICE_NAME **/
                            .moduleName(SERVICE_NAME)
                            /* serviceImpl 文件路径 **/
                            .serviceImpl(SERVICE_IMPL_PACK_NAME)
                            /* entity 文件路径 **/
                            .entity(ENTITY_PACK_NAME);
                })
                .strategyConfig(builder -> {
                    /* 设置需要生成的表名 **/
                    builder.addInclude(TABLE_NAMES)
                            /*  设置过滤表前缀 **/
//                            .addTablePrefix("t_", "c_") //
                            /* entity builder **/
                            .entityBuilder()
                            /* 数据库表映射到实体的命名策略-->下划线转驼峰 **/
                            .naming(NamingStrategy.underline_to_camel)
                            /* 数据库表字段映射到实体的命名策略-->下划线转驼峰 **/
                            .columnNaming(NamingStrategy.underline_to_camel)
                            /* 覆盖已有文件 **/
                            .enableFileOverride()
                            /* 开启lombok  **/
                            .enableLombok()
                            /* 开启链式模型 **/
                            .enableChainModel()
                            /* 开启继承父类 **/
//                            .superClass("BaseModel")
                            /* tableField 填充 **/
                            .addTableFills(Arrays.asList(
                                    /* 自定义字段填充策略 **/
                                    new Column("create_at", FieldFill.INSERT),
                                    new Column("update_at", FieldFill.UPDATE)
                            ))
//                            controller builder
                            .controllerBuilder()
                            .enableFileOverride()
                            /* 开启restful **/
                            .enableRestStyle()
                            /* 开启驼峰转连字符 **/
                            .enableHyphenStyle()
//                            service builder
                            .serviceBuilder()
                            /* 覆盖已有文件 **/
                            .enableFileOverride()
                            /* 自定义service命名策略 **/
                            .formatServiceFileName("%sService")
                            /* 自定义serviceImpl命名策略 **/
                            .formatServiceImplFileName("%sServiceImpl")
//                            mapper builder
                            .mapperBuilder()
                            .enableFileOverride()
                            /* 启用基础列列表,意味着在生成SQL查询时包括所有列的基础信息 **/
//                            .enableBaseColumnList()
                            /* 启用基础结果映射，即自动为查询结果创建默认的映射关系，将数据库表的字段映射到对象的属性上 **/
                            .enableBaseResultMap()
                            .build();
                })
                /* 使用Freemarker引擎模板，默认的是Velocity引擎模板 **/
//                .templateEngine(new FreemarkerTemplateEngine())
//                .templateConfig(builder -> {
//                    builder.entity(null)
//                            .build();
//                })

                /* 执行 **/
                .execute();
    }

}
