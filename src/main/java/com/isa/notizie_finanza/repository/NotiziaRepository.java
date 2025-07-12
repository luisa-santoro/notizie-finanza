package com.isa.notizie_finanza.repository;

import com.isa.notizie_finanza.model.Notizia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotiziaRepository extends JpaRepository<Notizia, Long> {

    @Query("SELECT n FROM Notizia n WHERE " +
            "(:titolo IS NULL OR LOWER(n.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
            "(:descrizione IS NULL OR LOWER(n.descrizione) LIKE LOWER(CONCAT('%', :descrizione, '%'))) AND " +
            "(:tag IS NULL OR LOWER(n.tag) LIKE LOWER(CONCAT('%', :tag, '%'))) AND " +
            "(:anno IS NULL OR FUNCTION('YEAR', n.dataPubblicazione) = :anno) AND " +
            "(:mese IS NULL OR FUNCTION('MONTH', n.dataPubblicazione) = :mese) AND " +
            "(:giorno IS NULL OR FUNCTION('DAY', n.dataPubblicazione) = :giorno)")
    List<Notizia> ricercaCompleta(
            @Param("titolo") String titolo,
            @Param("descrizione") String descrizione,
            @Param("tag") String tag,
            @Param("anno") Integer anno,
            @Param("mese") Integer mese,
            @Param("giorno") Integer giorno
    );
}
