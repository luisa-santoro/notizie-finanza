package com.isa.notizie_finanza.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isa.notizie_finanza.model.Notizia;
import com.isa.notizie_finanza.model.Utente;
import com.isa.notizie_finanza.service.NotiziaService;
import com.isa.notizie_finanza.service.S3Service;
import com.isa.notizie_finanza.service.UtenteService;

@RestController
@RequestMapping("/notizie")
public class NotiziaController {

    private final NotiziaService notiziaService;
    private final S3Service s3Service;
    private final UtenteService utenteService; // ➕ aggiunto per controllare il ruolo

    private static final Logger LOGGER = LoggerFactory.getLogger(NotiziaController.class);

    public NotiziaController(NotiziaService notiziaService, S3Service s3Service, UtenteService utenteService) {
        this.notiziaService = notiziaService;
        this.s3Service = s3Service;
        this.utenteService = utenteService;
    }

    @GetMapping
    public List<Notizia> getAll() {
        return notiziaService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notizia> getById(@PathVariable Long id) {
        Optional<Notizia> notizia = notiziaService.getById(id);
        return notizia.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notizia> updateNotizia(@PathVariable Long id, @RequestBody Notizia notiziaAggiornata) {
        Notizia updated = notiziaService.update(id, notiziaAggiornata);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotizia(@PathVariable Long id) {
        Optional<Notizia> notizia = notiziaService.getById(id);
        if (notizia.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        notiziaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Upload immagine su S3
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = s3Service.uploadFile(file);
        return ResponseEntity.ok(url);
    }

    // ✅ Crea notizia con immagine, controllo ruolo e autore
    @PostMapping("/crea-con-immagine")
    public ResponseEntity<Notizia> creaNotiziaConImmagine(
            @RequestPart("notizia") String notiziaJson,
            @RequestPart("file") MultipartFile file,
            @RequestParam("username") String username // ➕ chi manda la richiesta
    ) {
        try {
            // Trova l'utente
            Optional<Utente> utenteOpt = utenteService.findByUsername(username);
            if (utenteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Utente utente = utenteOpt.get();

            // Controllo ruolo: solo gli ADMIN possono creare notizie
            if (!"ADMIN".equalsIgnoreCase(utente.getRuolo())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Parsing della notizia da JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Notizia notizia = mapper.readValue(notiziaJson, Notizia.class);

            // Salva URL immagine
            String url = s3Service.uploadFile(file);
            notizia.setImmagini(url);

            // ➕ Salva l’autore
            notizia.setCreatore(username);

            Notizia creata = notiziaService.create(notizia);
            return ResponseEntity.status(HttpStatus.CREATED).body(creata);

        } catch (JsonProcessingException e) {
            LOGGER.error("Errore nel parsing JSON", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            LOGGER.error("Errore interno", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
