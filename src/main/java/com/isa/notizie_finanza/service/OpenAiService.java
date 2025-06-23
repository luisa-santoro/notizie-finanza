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

    public String generaTitolo(String testo) throws Exception {
        String prompt = "Genera un titolo accattivante per il seguente testo:\n" + testo;
        return generateFromText(prompt).trim();
    }

    public String generaRiassunto(String testo) throws Exception {
        String prompt = "Fornisci un riassunto tra 200 e 600 caratteri per il seguente testo:\n" + testo;
        return generateFromText(prompt).trim();
    }

    public String generaTag(String testo) throws Exception {
        String prompt = "Elenca da 3 a 5 parole chiave (tag) separate da virgola per il seguente testo:\n" + testo;
        return generateFromText(prompt).trim();
    }

}
