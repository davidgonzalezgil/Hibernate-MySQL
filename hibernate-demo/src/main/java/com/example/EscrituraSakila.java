package com.example;

import java.math.BigDecimal;

import org.hibernate.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class EscrituraSakila {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("aulaPU")) {

            // INSERT con persist y commit
            int idGenerado = ejecutarTransaccion(emf, em -> {
                if (em.find(Idioma.class, 1) == null) {
                    throw new IllegalStateException("El idioma 1 debe existir");
                }
                Pelicula nueva = new Pelicula("PRACTICA HQL 2DAM", 1, new BigDecimal("2.99"));
                em.persist(nueva);
                em.flush();
                return nueva.getId();
            }, true);
            verificarEnBD(emf, idGenerado, "1. Tras ALTA (commit)");

            // MODIFICACIÓN CON SETTER
            ejecutarTransaccion(emf, em -> {
                Pelicula p = em.find(Pelicula.class, idGenerado);
                if (p == null || !"PRACTICA HQL 2DAM".equals(p.getTitulo())) {
                    throw new IllegalStateException("Comprueba el ID de tu película");
                }
                p.setTarifa(new BigDecimal("3.49"));
                return idGenerado;
            }, true);
            verificarEnBD(emf, idGenerado, "2. Tras SETTER a 3.49 (commit)");

            // PRUEBA DE ROLLBACK
            ejecutarTransaccion(emf, em -> {
                Pelicula p = em.find(Pelicula.class, idGenerado);
                p.setTarifa(new BigDecimal("4.49"));
                em.flush();
                return idGenerado;
            }, false); // false fuerza tx.rollback()
            verificarEnBD(emf, idGenerado, "3. Tras SETTER a 4.49 (rollback -> conserva 3.49)");

            // UPDATE HQL
            ejecutarTransaccion(emf, em -> {
                Session sesion = em.unwrap(Session.class);
                int afectadas = sesion.createMutationQuery("""
                        update Pelicula p
                        set p.tarifa = :tarifa
                        where p.id = :id and p.titulo = :titulo
                        """)
                        .setParameter("tarifa", new BigDecimal("3.99"))
                        .setParameter("id", idGenerado)
                        .setParameter("titulo", "PRACTICA HQL 2DAM")
                        .executeUpdate();
                if (afectadas != 1) {
                    throw new IllegalStateException("Entidades afectadas: " + afectadas);
                }
                System.out.println("Entidades afectadas por UPDATE HQL: " + afectadas);
                return idGenerado;
            }, true);
            verificarEnBD(emf, idGenerado, "4. Tras UPDATE HQL a 3.99 (commit)");
        }
    }

    @FunctionalInterface
    private interface Operacion {
        int ejecutar(EntityManager em);
    }

    private static int ejecutarTransaccion(EntityManagerFactory emf, Operacion op, boolean confirmar) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                int resultado = op.ejecutar(em);
                if (confirmar) {
                    tx.commit();
                } else {
                    tx.rollback();
                }
                return resultado;
            } catch (RuntimeException e) {
                if (tx.isActive())
                    tx.rollback();
                throw e;
            }
        }
    }

    private static void verificarEnBD(EntityManagerFactory emf, int id, String etiqueta) {
        try (EntityManager em = emf.createEntityManager()) {
            Pelicula p = em.find(Pelicula.class, id);
            System.out.println(etiqueta + " -> " + (p == null ? "Sin fila para ID " + id
                    : p.getId() + " | " + p.getTitulo() + " | " + p.getTarifa()));
        }
    }
}