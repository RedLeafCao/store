package com.can.store.shopping;

import com.can.store.shopping.commons.kiss.Config;
import com.can.store.shopping.commons.kiss.Initd;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.can.store.shopping.controller"})
public class ShoppingApplication {

    public static void main(String[] args) {
        try {
            Config.startChecking();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Initd.init();
        SpringApplication.run(ShoppingApplication.class, args);
    }

}
