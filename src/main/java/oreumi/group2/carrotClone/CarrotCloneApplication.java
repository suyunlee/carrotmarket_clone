package oreumi.group2.carrotClone;

import oreumi.group2.carrotClone.Config.GeminiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GeminiProperties.class)
public class CarrotCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarrotCloneApplication.class, args);
	}
}