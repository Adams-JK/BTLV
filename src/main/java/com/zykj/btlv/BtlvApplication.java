package com.zykj.btlv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableSwagger2
@EnableTransactionManagement
public class BtlvApplication {

    public static void main(String[] args) {
        SpringApplication.run(BtlvApplication.class, args);
    }

}
