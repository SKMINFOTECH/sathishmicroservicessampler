package me.sathish.accountholdercommand;

import me.sathish.accountholdercommand.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class AccountHolderCommandApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountHolderCommandApplication.class, args);
    }
}
