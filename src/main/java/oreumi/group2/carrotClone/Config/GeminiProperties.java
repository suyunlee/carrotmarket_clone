package oreumi.group2.carrotClone.Config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
@Data
public class GeminiProperties {
    @Value("${google.ai.api.key}")
    private String apikey;
    private String model =  "models/gemini-2.5-flash";
    private double temperature = 0.7;
    private int topK = 1;
}