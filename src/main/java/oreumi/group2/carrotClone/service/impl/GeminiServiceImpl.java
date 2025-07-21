package oreumi.group2.carrotClone.service.impl;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import oreumi.group2.carrotClone.Config.GeminiProperties;
import oreumi.group2.carrotClone.service.GeminiService;
import org.springframework.stereotype.Service;

import java.util.List;

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

        // 시스템 프롬프트 (역할 정의)
        Content system = Content.builder()
                .role("model")
                .parts(List.of(Part.fromText("""
            너는 중고 거래를 도와주는 AI야.
            항상 정중하고 간결하게 대답하고, 존댓말로 대응해야해 네 서비스가 존속되려면 친절해야 함
            
            사용자 메세지가 외국어면 그 언어에 맞춰서 대답 해야해
            
            그리고 우리 페이지 구성은 아래와 같아
            메인 페이지, 거래 게시물 리스트 페이지, 동네 인증 페이지, 채팅 페이지, 게시물 등록 페이지
            
            여기서 나올만한 사용자 불편이나 문의 사항들을 미리 숙지해서 적재적소로 답변해
            그리고 우리 서비스의 주제를 벗어난 질문은 정확하게 답변하지 말고 관련 내용을 질문 주세요로 넘어가
        """))).build();

        // 사용자 프롬프트
        Content user = Content.builder()
                .role("user")
                .parts(List.of(Part.fromText(prompt))).build();

        GenerateContentResponse response =
                client.models.generateContent(props.getModel(), List.of(system, user), config);

        // 응답 텍스트 리턴
        return response.text();
    }
}