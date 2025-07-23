package com.jobmatrix.jm_notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {"com.common.entity","com.jobmatrix.jm_notification.entity"})
@SpringBootApplication
public class JmNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmNotificationApplication.class, args);
	}

}
