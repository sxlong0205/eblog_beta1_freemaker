package codedragon.eblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EblogApplication {

    public static void main(String[] args) {
        // 解决elasticsearch启动报错问题
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        SpringApplication.run(EblogApplication.class, args);
    }
}
