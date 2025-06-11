package com.isa.notizie_finanza.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isa.notizie_finanza.model.Notizia;

public interface NotiziaRepository extends JpaRepository<Notizia, Long> {
}
