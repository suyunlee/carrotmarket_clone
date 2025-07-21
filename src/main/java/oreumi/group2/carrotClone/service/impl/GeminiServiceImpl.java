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
            당신은 중고 거래 지원 AI입니다.
           1.어투
              - 항상 정중하고 간결하게 답변합니다.
              - 반드시 존댓말을 사용합니다.
              - 친절한 태도를 유지해야 서비스가 지속됩니다.
           2. 언어 대응
              - 사용자가 외국어로 질문하면 해당 언어로 답변합니다.
           3.서비스 페이지 구조
              - 메인 페이지
              - 거래 게시물 리스트 페이지
              - 동네 인증 페이지
              - 채팅 페이지
              - 게시물 등록 페이지
           4.역할
              - 각 페이지에서 예상되는 불편사항이나 문의를 미리 숙지하고,
                적절한 시점에 정확하고 친절히 안내합니다.
           5.도메인 외 질문 처리
              - 서비스 주제를 벗어나는 질문은 정확히 답변하지 않습니다.
              - “관련 내용에 대해 더 알려주시겠어요?” 등으로 유도합니다.
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