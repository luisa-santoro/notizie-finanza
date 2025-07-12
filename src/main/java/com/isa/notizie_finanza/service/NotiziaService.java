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
            Notizia notiziaEsistente = notiziaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Notizia non trovata con id " + id));

            // Debug: stampa versioni anche se null
            System.out.println(" Versione ricevuta (potrebbe essere null): " + notiziaAggiornata.getVersion());
            System.out.println(" Versione nel DB: " + notiziaEsistente.getVersion());

            // Se la versione ricevuta è null → errore esplicito
            if (notiziaAggiornata.getVersion() == null) {
                throw new NotiziaConflictException("La versione inviata è nulla. Verifica il JSON.");
            }

            if (!notiziaAggiornata.getVersion().equals(notiziaEsistente.getVersion())) {
                throw new NotiziaConflictException("Versione non aggiornata, la notizia è stata modificata da un altro utente.");
            }

            // Aggiorna i campi
            notiziaEsistente.setTitolo(notiziaAggiornata.getTitolo());
            notiziaEsistente.setDescrizione(notiziaAggiornata.getDescrizione());
            notiziaEsistente.setImmagini(notiziaAggiornata.getImmagini());
            notiziaEsistente.setTag(notiziaAggiornata.getTag());
            notiziaEsistente.setDataPubblicazione(notiziaAggiornata.getDataPubblicazione());
            notiziaEsistente.setCategoria(notiziaAggiornata.getCategoria());

            return notiziaRepository.save(notiziaEsistente);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new NotiziaConflictException("La notizia è stata modificata da un altro utente. Riprova.");
        }
    }

//  Metodo di ricerca avanzata
    public List<Notizia> cercaNotizie(String titolo, String descrizione, String tag,
                                      Integer anno, Integer mese, Integer giorno) {
        return notiziaRepository.ricercaCompleta(titolo, descrizione, tag, anno, mese, giorno);
    }



}


