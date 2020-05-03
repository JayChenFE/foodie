package com.github.jaychenfe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author jaychenfe
 */
@MapperScan(basePackages = "com.github.jaychenfe.mapper")
@ComponentScan(basePackages = {"com.github.jaychenfe", "org.n3r.idworker"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
