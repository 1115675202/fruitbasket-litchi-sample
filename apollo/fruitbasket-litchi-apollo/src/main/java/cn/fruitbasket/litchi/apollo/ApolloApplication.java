package cn.fruitbasket.litchi.apollo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuBing
 * @date 2021/12/7
 */
@RestController
@EnableApolloConfig
@SpringBootApplication
public class ApolloApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApolloApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApolloApplication.class, args);
    }

    @Autowired
    private Environment environment;

    @GetMapping
    public String test() {
        String ret = String.format("env=%s, logging.level.root=%s"
                , environment.getProperty("env"), environment.getProperty("logging.level.root"));
        logger.debug(ret);
        return ret;
    }
}
