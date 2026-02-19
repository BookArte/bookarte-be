package com.library.bookarte.ai.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.library.bookarte.ai.dto.request.GeminiRequest;
import com.library.bookarte.ai.dto.response.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final Client client;

    private static final String SYSTEM_INSTRUCTION_TEXT =
            "당신은 'BookArte(북아티)' 도서사이트의 전문 챗봇입니다.\n" +
                    "1. 오직 도서관 서비스, 도서 추천, 대출 방법 등 도서관과 관련된 질문에만 답하세요.\n" +
                    "2. 도서관과 관련 없는 일상 대화나 질문에는 '죄송합니다. 저는 북아티 도서관 관련 안내만 도와드릴 수 있습니다.'라고 정중히 거절하세요.\n" +
                    "3. 말투는 친절하고 정중한 '사서'의 톤을 유지하세요.\n" +
                    "4. 우리 도서관의 특징: 24시간 무인 반납 가능, 희망 도서 신청 가능.";

    public GeminiResponse getChateResponse(GeminiRequest geminiRequest) {
        Content systemInstruction = Content.builder()
                .parts(Collections.singletonList(Part.builder().text(SYSTEM_INSTRUCTION_TEXT).build()))
                .role("system")
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(systemInstruction)
                .temperature(0.5f)
                .build();

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                geminiRequest.getInputMessage(),
                config
        );
        return GeminiResponse.builder()
                .answer(response.text())
                .build();
    }
}
