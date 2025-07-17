package oreumi.group2.carrotClone.service.impl;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import oreumi.group2.carrotClone.Config.GeminiProperties;
import oreumi.group2.carrotClone.service.GeminiService;
import org.springframework.stereotype.Service;

@Service
public class GeminiServiceImpl implements GeminiService {
    private final Client client;
    private final GeminiProperties props;

    public GeminiServiceImpl(GeminiProperties props) {
        this.props = props;
        this.client = Client.builder()
                .apiKey(props.getApikey())
                .build();
    }

    @Override
    public String generateReply(String prompt) {

        int k = Math.max(1, Math.min(props.getTopK(), 8));

        // GenerateContentConfig 에 temperature, topK 설정
        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature((float) props.getTemperature())
                .candidateCount(k)
                .build();

        GenerateContentResponse response =
                client.models.generateContent(props.getModel(), prompt, config);

        // 응답 텍스트 리턴
        return response.text();
    }
}