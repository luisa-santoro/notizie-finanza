package com.isa.notizie_finanza.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;



@Entity
@Table(name = "news")
public class Notizia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    
    private String immagini;

    private String tag;

    private LocalDateTime dataPubblicazione;

    @Column(nullable = false)
    private String categoria;


    @Version
    private Integer version; // Campo per optimistic locking
    
    private String creatore;
    
    public String getCreatore() {
    return creatore;
    }

    public void setCreatore(String creatore) {
    this.creatore = creatore;
    }



    public Notizia() {
        // Costruttore vuoto richiesto da JPA
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getImmagini() {
        return immagini;
    }
    public void setImmagini(String immagini) {
        this.immagini = immagini;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getDataPubblicazione() {
        return dataPubblicazione;
    }
    public void setDataPubblicazione(LocalDateTime dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

}
