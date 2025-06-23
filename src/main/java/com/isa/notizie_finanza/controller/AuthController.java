package com.isa.notizie_finanza.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isa.notizie_finanza.model.Utente;
import com.isa.notizie_finanza.service.UtenteService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UtenteService utenteService;

    public AuthController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    //  Login - verifica password hashata
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credenziali) {
        String username = credenziali.get("username");
        String password = credenziali.get("password");

        Optional<Utente> utenteOpt = utenteService.findByUsername(username);

        // Verifica se l'utente esiste e la password hashata combacia
        if (utenteOpt.isPresent() && BCrypt.checkpw(password, utenteOpt.get().getPassword())) {
            return ResponseEntity.ok("Login riuscito!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali errate");
        }
    }

    // Endpoint per creare un utente e salvare la password hashata
   


@PostMapping("/register")
public ResponseEntity<String> register(@RequestBody Utente nuovoUtente) {
    // Criptiamo la password prima di salvarla
    String hashedPassword = BCrypt.hashpw(nuovoUtente.getPassword(), BCrypt.gensalt());
    nuovoUtente.setPassword(hashedPassword);

    // Ruolo di default se non specificato
    if (nuovoUtente.getRuolo() == null) {
        nuovoUtente.setRuolo("USER");
    }

    utenteService.salvaUtente(nuovoUtente);
    return ResponseEntity.ok("Registrazione completata con successo!");
}

}