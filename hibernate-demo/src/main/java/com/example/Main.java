package com.example;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        try {
            // lecturas
            System.out.println("Actores");
            List<Actor> actores = em.createQuery("SELECT a FROM Actor a", Actor.class)
                    .setMaxResults(10)
                    .getResultList();

            for (Actor actor : actores) {
                System.out.println(actor);
            }

            System.out.println("Peliculas");
            List<Pelicula> peliculas = em.createQuery("SELECT p FROM Pelicula p", Pelicula.class)
                    .setMaxResults(10)
                    .getResultList();

            for (Pelicula p : peliculas) {
                System.out.println(p);
            }

            // modificar y deshacer
            System.out.println("prueba de update y rollback");
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Pelicula pelicula = em.find(Pelicula.class, (short) 1);
                if (pelicula != null) {
                    BigDecimal anterior = pelicula.getTarifa();
                    pelicula.setTarifa(anterior.add(new BigDecimal("0.10")));
                    em.flush();
                    System.out.println("Antes: " + anterior);
                    System.out.println("En la transaccion: " + pelicula.getTarifa());
                }
                tx.rollback();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }

            try (EntityManager verificacion = emf.createEntityManager()) {
                Pelicula pelicula = verificacion.find(Pelicula.class, (short) 1);
                if (pelicula != null) {
                    System.out.println("Tras rollback: " + pelicula.getTarifa());
                }
            }
        } finally {
            em.close();
            emf.close();
        }
    }
}