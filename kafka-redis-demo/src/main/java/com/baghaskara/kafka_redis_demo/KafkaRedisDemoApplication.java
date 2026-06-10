package com.baghaskara.kafka_redis_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
public class KafkaRedisDemoApplication {

    public static void main(String[] args) {
        // Bootstraps the Spring ApplicationContext.
        // Equivalent to setting up router and http.ListenAndServe(":8080", router) in Go.
        SpringApplication.run(KafkaRedisDemoApplication.class, args);
    }
}