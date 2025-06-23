package com.isa.notizie_finanza.controller;

import com.isa.notizie_finanza.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final OpenAiService openAiService;

    public AIController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/elabora")
    public String generaContenuti(@RequestBody String testo) {
        try {
            String prompt = "Dato il seguente testo:\n" + testo + "\n\n" +
                    "1. Genera un titolo accattivante.\n" +
                    "2. Crea un riassunto tra 200 e 600 caratteri.\n" +
                    "3. Elenca da 3 a 5 parole chiave (tag) separate da virgola.";

            return openAiService.generateFromText(prompt);
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }
}
