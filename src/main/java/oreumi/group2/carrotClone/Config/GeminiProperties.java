package oreumi.group2.carrotClone.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
@Data
public class GeminiProperties {

    private String apikey;
    private String model;
    private double temperature;
    private int topK;
}