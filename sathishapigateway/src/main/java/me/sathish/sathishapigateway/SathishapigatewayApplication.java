package me.sathish.sathishapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SathishapigatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SathishapigatewayApplication.class, args);
    }

}
