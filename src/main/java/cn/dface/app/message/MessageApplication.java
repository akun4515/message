package cn.dface.app.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class MessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }
}
