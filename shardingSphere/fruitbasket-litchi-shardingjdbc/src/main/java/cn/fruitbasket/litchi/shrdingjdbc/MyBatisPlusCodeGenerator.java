package cn.fruitbasket.litchi.shrdingjdbc;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * MyBatis-Plus 代码生成
 *
 * @author LiuBing
 * @since 2021/8/5
 */
public class MyBatisPlusCodeGenerator {

    private static final String DATABASE_URL = "jdbc:mysql://mysql_node0:3306/order_database?useUnicode=true&useSSL=false&characterEncoding=utf8";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "root";
    private static final String PARENT_PACKAGE = "cn.fruitbasket.litchi.shrdingjdbc";
    private static final String AUTHOR = "LiuBing";

    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/shardingSphere/fruitbasket-litchi-shardingjdbc/src/main/java";
    private static final String MODULE_NAME = "";
    private static final String[] TABEL_NAMES = new String[]{"t_order"};

    public static void main(String[] args) {
        PackageConfig packageConfig = packageConfig();

        new AutoGenerator()
                // 全局配置
                .setGlobalConfig(globalConfig())
                // 数据源配置
                .setDataSource(dataSourceConfig())
                // 包配置
                .setPackageInfo(packageConfig)
                // 策略配置
                .setStrategy(strategyConfig(packageConfig))
                .setTemplateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    public static GlobalConfig globalConfig() {
        return new GlobalConfig()
                .setOutputDir(OUTPUT_DIR)
                .setAuthor(AUTHOR)
                .setOpen(false)
                .setFileOverride(true)
                ;
    }

    public static DataSourceConfig dataSourceConfig() {
        return new DataSourceConfig()
                .setUrl(DATABASE_URL)
                .setDriverName("com.mysql.cj.jdbc.Driver")
                .setUsername(DATABASE_USERNAME)
                .setPassword(DATABASE_PASSWORD);
    }

    public static PackageConfig packageConfig() {
        return new PackageConfig()
                .setModuleName(MODULE_NAME)
                .setParent(PARENT_PACKAGE);
    }

    public static StrategyConfig strategyConfig(PackageConfig packageConfig) {
        return new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
//        .setSuperEntityClass("你自己的父类实体,没有就不用设置!")
                .setRestControllerStyle(true)
//        .setSuperControllerClass("你自己的父类控制器,没有就不用设置!")
//                .setSuperEntityColumns("id")
                .setInclude(TABEL_NAMES)
                .setControllerMappingHyphenStyle(true)
                .setTablePrefix(packageConfig.getModuleName() + "_")
                .setSkipView(true)
                .setEntityColumnConstant(true)
                .setEntityLombokModel(true)
                .setChainModel(true)
                ;
    }

    private MyBatisPlusCodeGenerator() {
    }
}
