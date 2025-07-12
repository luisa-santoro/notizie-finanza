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
import com.isa.notizie_finanza.service.OpenAiService;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/notizie")
public class NotiziaController {

    private final NotiziaService notiziaService;
    private final S3Service s3Service;
    private final UtenteService utenteService; // ➕ aggiunto per controllare il ruolo
    private final OpenAiService aiService;


    private static final Logger LOGGER = LoggerFactory.getLogger(NotiziaController.class);

    public NotiziaController(NotiziaService notiziaService, S3Service s3Service, UtenteService utenteService,OpenAiService aiService) {
        this.notiziaService = notiziaService;
        this.s3Service = s3Service;
        this.utenteService = utenteService;
        this.aiService = aiService;
    }


    // 🔁 REDIRECT /notizie → /notizie/tutte
    @GetMapping(value = {"", "/"})
    public ResponseEntity<Void> redirectNotizie() {
        return ResponseEntity.status(302)
                .header("Location", "/notizie/tutte")
                .build();
    }


    @GetMapping("/tutte")
    public List<Notizia> getAll() {
        return notiziaService.getAll();
    }

    // Ricerca avanzata delle notizie
    @GetMapping("/cerca")
    public ResponseEntity<List<Notizia>> cercaNotizie(
            @RequestParam(required = false) String titolo,
            @RequestParam(required = false) String descrizione,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) Integer mese,
            @RequestParam(required = false) Integer giorno
    ) {
        List<Notizia> risultati = notiziaService.cercaNotizie(titolo, descrizione, tag, anno, mese, giorno);
        return ResponseEntity.ok(risultati);
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
            @RequestParam("username") String username
    ) {
        try {
            Optional<Utente> utenteOpt = utenteService.findByUsername(username);
            if (utenteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Utente utente = utenteOpt.get();
            if (!"ADMIN".equalsIgnoreCase(utente.getRuolo())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Notizia notizia = mapper.readValue(notiziaJson, Notizia.class);

            // Upload immagine su S3
            String url = s3Service.uploadFile(file);
            notizia.setImmagini(url);

            // Aggiungi username come creatore
            notizia.setCreatore(username);

            // 🧠 Genera AI: titolo, riassunto, tag
            String testoDaAnalizzare = notizia.getDescrizione();  // il campo descrizione è la base

            String titolo = aiService.generaTitolo(testoDaAnalizzare);
            String riassunto = aiService.generaRiassunto(testoDaAnalizzare);
            String tag = aiService.generaTag(testoDaAnalizzare);

            notizia.setTitolo(titolo);
            notizia.setTag(tag); // campo "fonte" usato per i tag
            notizia.setDescrizione(riassunto); // sovrascriviamo con il riassunto

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


