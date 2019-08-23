package com.neuedu.business_interface;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.neuedu.business_interface.dao")
public class BusinessInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessInterfaceApplication.class, args);
    }

}
