package com.isa.notizie_finanza;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiziaRepository extends JpaRepository<Notizia, Long> {
    // Puoi aggiungere metodi personalizzati se vuoi
}
