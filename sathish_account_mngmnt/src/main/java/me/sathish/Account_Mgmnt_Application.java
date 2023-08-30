package me.sathish;

import me.sathish.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({ApplicationProperties.class})
public class Account_Mgmnt_Application {

    public static void main(String[] args) {
        SpringApplication.run(Account_Mgmnt_Application.class, args);
    }
}
