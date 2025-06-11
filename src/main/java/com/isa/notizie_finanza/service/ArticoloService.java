package com.isa.notizie_finanza.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.isa.notizie_finanza.model.Articolo;
import com.isa.notizie_finanza.repository.ArticoloRepository;

@Service
public class ArticoloService {

    private final ArticoloRepository articoloRepository;

    public ArticoloService(ArticoloRepository articoloRepository) {
        this.articoloRepository = articoloRepository;
    }

    public List<Articolo> getAllArticoli() {
        return articoloRepository.findAll();
    }

    public Optional<Articolo> getArticoloById(Long id) {
        return articoloRepository.findById(id);
    }

    public Articolo createArticolo(Articolo articolo) {
        return articoloRepository.save(articolo);
    }

    public void deleteArticolo(Long id) {
        articoloRepository.deleteById(id);
    }
}
