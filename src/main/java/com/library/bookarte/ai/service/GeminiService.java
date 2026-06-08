package com.library.bookarte.ai.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.library.bookarte.ai.dto.request.GeminiRequest;
import com.library.bookarte.ai.dto.response.GeminiResponse;
import com.library.bookarte.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final MemberRepository memberRepository;

    private final Client client;

    private final Map<String, List<Content>> chatHistories = new ConcurrentHashMap<>();

    private static final String SYSTEM_INSTRUCTION_TEXT =
            "당신은 'BookArte(북아티)' 도서사이트의 전문 챗봇입니다.\n" +
                    "1. 오직 도서관 서비스, 도서 추천, 대출 방법 등 도서관과 관련된 질문에만 답하세요.\n" +
                    "2. 도서관과 관련 없는 일상 대화나 질문에는 '죄송합니다. 저는 북아티 도서관 관련 안내만 도와드릴 수 있습니다.'라고 정중히 거절하세요.\n" +
                    "3. 말투는 친절하고 정중한 '사서'의 톤을 유지하세요.\n" +
                    "4. 우리 도서관의 특징: 24시간 무인 반납 가능, 희망 도서 신청 가능." +
                    "5. 이전 대화 맥락을 기억하여 답변하세요.";

    public GeminiResponse getChateResponse(Long memberId, GeminiRequest geminiRequest, HttpServletRequest request) {
        Content systemInstruction = Content.builder()
                .parts(Collections.singletonList(Part.builder().text(SYSTEM_INSTRUCTION_TEXT).build()))
                .role("system")
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(systemInstruction)
                .temperature(0.5f)
                .build();

        String identifier;

        if (memberId != null) {
            identifier = memberRepository.findMemberUserIdByMemberId(memberId)
                    .orElseGet(() -> request.getSession().getId());
        } else {
            identifier = request.getSession().getId();
        }

        List<Content> history = chatHistories.computeIfAbsent(identifier, k -> new ArrayList<>());

        Content userMessage = Content.builder()
                .role("user")
                .parts(Collections.singletonList(Part.builder().text(geminiRequest.getInputMessage()).build()))
                .build();
        history.add(userMessage);

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                history,
                config
        );

        String answerText = response.text();

        Content modelResponse = Content.builder()
                .role("model")
                .parts(Collections.singletonList(Part.builder().text(answerText).build()))
                .build();
        history.add(modelResponse);

        if (history.size() > 20) {
            history.subList(0, 2).clear();
        }

        return GeminiResponse.builder()
                .sender("ai")
                .message(answerText)
                .build();
    }

    public List<GeminiResponse> getChatHistory(Long memberId, HttpServletRequest request) {
        String identifier;
        if (memberId != null) {
            identifier = memberRepository.findMemberUserIdByMemberId(memberId)
                    .orElseGet(() -> request.getSession().getId());
        } else {
            identifier = request.getSession().getId();
        }

        List<Content> history = chatHistories.getOrDefault(identifier, Collections.emptyList());

        return history.stream().map(content -> {
            String role = content.role().orElse("user");

            String text = "";
            if (content.parts().isPresent()) {
                List<Part> partsList = content.parts().get();
                if (!partsList.isEmpty()) {
                    text = partsList.get(0).text().orElse("");
                }
            }

            String sender = "model".equals(role) ? "ai" : "user";

            return GeminiResponse.builder()
                    .sender(sender)
                    .message(text)
                    .build();
        }).toList();
    }

    public void clearChatHistory(String sessionId) {
        chatHistories.remove(sessionId);
    }
}
