package com.isa.notizie_finanza.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;
import java.util.*;

@Service
public class OpenAiService {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    private final ObjectMapper mapper = new ObjectMapper();

    public String generateFromText(String prompt) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Corpo JSON della richiesta
        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message),
                "temperature", 0.7
        );


        String requestJson = mapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parsing JSON per ottenere la risposta
        Map<String, Object> result = mapper.readValue(response.body(), Map.class);
        Map<String, Object> choice = ((List<Map<String, Object>>) result.get("choices")).get(0);
        Map<String, String> messageMap = (Map<String, String>) choice.get("message");

        return messageMap.get("content");
    }
}
