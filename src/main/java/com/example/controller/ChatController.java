package com.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.services.ChatService;

import java.util.Map;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String message) {
        return ResponseEntity.ok(chatService.sendMessage(message));
    }

    // For frontend POST requests (e.g., from index.html)
    @PostMapping("/chat")
    public ResponseEntity<String> chatPost(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        return ResponseEntity.ok(chatService.sendMessage(message));
    }
}

