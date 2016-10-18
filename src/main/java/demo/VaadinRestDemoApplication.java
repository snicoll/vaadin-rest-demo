package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(VaadinRestDemoProperties.class)
@EnableCaching
public class VaadinRestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaadinRestDemoApplication.class, args);
	}
}
