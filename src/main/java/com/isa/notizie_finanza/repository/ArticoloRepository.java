package com.isa.notizie_finanza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isa.notizie_finanza.model.Articolo;

@Repository
public interface ArticoloRepository extends JpaRepository<Articolo, Long> {
    //  aggiungere metodi personalizzati se necessario
}
