package com.isa.notizie_finanza.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isa.notizie_finanza.model.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Utente findByUsername(String username);
}
