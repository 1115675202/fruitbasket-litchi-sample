package cn.fruitbasket.litchi.shrdingjdbc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author LiuBing
 * @date 2021/11/26
 */
@SpringBootApplication
@MapperScan("cn.fruitbasket.litchi.shrdingjdbc.mapper")
public class ShardingJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcApplication.class, args);
    }
}
