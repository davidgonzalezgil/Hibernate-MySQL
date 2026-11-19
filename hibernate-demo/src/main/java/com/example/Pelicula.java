package com.example;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "film")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Short id;

    @Column(name = "title", nullable = false, length = 128)
    private String titulo;

    @Column(name = "language_id", nullable = false)
    private Byte idiomaId;

    @Column(name = "rental_rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal tarifa;

    public Pelicula() {
    }

    public Short getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Byte getIdiomaId() {
        return idiomaId;
    }

    public void setIdiomaId(Byte idiomaId) {
        this.idiomaId = idiomaId;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    @Override
    public String toString() {
        return id + " - " + titulo + " (" + tarifa + " €)";
    }
}