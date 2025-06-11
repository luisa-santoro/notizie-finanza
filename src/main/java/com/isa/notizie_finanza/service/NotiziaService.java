package com.isa.notizie_finanza.service;

import java.util.List;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.isa.notizie_finanza.exception.NotiziaConflictException;
import com.isa.notizie_finanza.model.Notizia;
import com.isa.notizie_finanza.repository.NotiziaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class NotiziaService {

    private final NotiziaRepository notiziaRepository;

    public NotiziaService(NotiziaRepository notiziaRepository) {
        this.notiziaRepository = notiziaRepository;
    }

    public List<Notizia> getAll() {
        return notiziaRepository.findAll();
    }

    public Optional<Notizia> getById(Long id) {
        return notiziaRepository.findById(id);
    }

    public Notizia create(Notizia notizia) {
        return notiziaRepository.save(notizia);
    }

    public void delete(Long id) {
        notiziaRepository.deleteById(id);
    }

    public Notizia update(Long id, Notizia notiziaAggiornata) {
    try {
        // Verifica se esiste la notizia
        Notizia notiziaEsistente = notiziaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notizia non trovata con id " + id));

        // Controlla se la versione inviata corrisponde
        if (!notiziaAggiornata.getVersion().equals(notiziaEsistente.getVersion())) {
            throw new NotiziaConflictException("Versione non aggiornata, la notizia è stata modificata da un altro utente.");
        }

        // Aggiorna i campi
        notiziaEsistente.setTitolo(notiziaAggiornata.getTitolo());
        notiziaEsistente.setDescrizione(notiziaAggiornata.getDescrizione());
        notiziaEsistente.setImmagini(notiziaAggiornata.getImmagini());
        notiziaEsistente.setFonte(notiziaAggiornata.getFonte());
        notiziaEsistente.setDataPubblicazione(notiziaAggiornata.getDataPubblicazione());

        // Salva, Hibernate aggiornerà la versione incrementandola di 1
        return notiziaRepository.save(notiziaEsistente);

    } catch (ObjectOptimisticLockingFailureException e) {
        throw new NotiziaConflictException("La notizia è stata modificata da un altro utente. Riprova.");
    }
}


}


