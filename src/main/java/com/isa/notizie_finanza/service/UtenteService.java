package com.isa.notizie_finanza.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.isa.notizie_finanza.model.Utente;
import com.isa.notizie_finanza.repository.UtenteRepository;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    // 🔐 Salvataggio con hashing password
    public Utente salvaUtente(Utente utente) {
        String passwordHash = BCrypt.hashpw(utente.getPassword(), BCrypt.gensalt());
        utente.setPassword(passwordHash);
        return utenteRepository.save(utente);
    }

    // 🔍 Ricerca utente per login
    public Optional<Utente> findByUsername(String username) {
        return Optional.ofNullable(utenteRepository.findByUsername(username));
    }

    public void save(Utente utente) {
    utenteRepository.save(utente);
    }

}