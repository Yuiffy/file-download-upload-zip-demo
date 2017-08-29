package com.dyf;

import com.dyf.download.storage.StorageProperties;
import com.dyf.download.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class App {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

    @Bean
    CommandLineRunner init(final StorageService storageService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                storageService.deleteAll();
                storageService.init();
            }
        };
    }
}
