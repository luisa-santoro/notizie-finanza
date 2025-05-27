package com.isa.notizie_finanza;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notizie")
public class NotiziaController {

    private final NotiziaRepository repository;

    // Costruttore per iniettare la dipendenza
    public NotiziaController(NotiziaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Notizia> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Notizia create(@RequestBody Notizia notizia) {
        return repository.save(notizia);
    }
}
