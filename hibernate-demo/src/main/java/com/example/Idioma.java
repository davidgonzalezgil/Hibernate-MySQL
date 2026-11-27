package com.example;

import jakarta.persistence.*;

@Entity
@Table(name = "language")
public class Idioma {

    @Id
    @Column(name = "language_id")
    private Integer id;

    @Column(name = "name")
    private String nombre;

    protected Idioma() {
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}