package com.example.services;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ChatService {

    private final WebClient webClient;
    private final List<Map<String, String>> chatHistory = new ArrayList<>();
    @Value("${huggingface.token}")
    private String token = "REPLACE_WITH_MY_HUGGINGFACE_TOKEN"; 

    public ChatService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://router.huggingface.co/v1")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        // Optional: system prompt
        chatHistory.add(Map.of(
            "role", "system",
            "content", "You are a helpful and witty chatbot assistant. But please send information that is Singapore based."
        ));
    }

    public String sendMessage(String userMessage) {
        chatHistory.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> request = Map.of(
            "model", "google/gemma-3-27b-it:nebius",
            "messages", new ArrayList<>(chatHistory)  // send full history
        );

        try {
            String raw = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(raw);
            String assistantReply = root.path("choices").get(0).path("message").path("content").asText();

            chatHistory.add(Map.of("role", "assistant", "content", assistantReply));

            return assistantReply;

        } catch (WebClientResponseException e) {
            return "API Error: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "General Error: " + e.getMessage();
        }
    }
}




