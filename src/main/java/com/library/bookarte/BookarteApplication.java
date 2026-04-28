package com.library.bookarte;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@EnableJpaAuditing
@SpringBootApplication(exclude = {io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration.class})
public class BookarteApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookarteApplication.class, args);
	}

}
