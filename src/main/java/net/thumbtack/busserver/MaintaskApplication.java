package net.thumbtack.busserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MaintaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaintaskApplication.class, args);
	}

}
