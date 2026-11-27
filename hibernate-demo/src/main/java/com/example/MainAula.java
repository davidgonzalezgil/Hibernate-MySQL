package com.example;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;

public class MainAula {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("aulaPU");
                EntityManager em = emf.createEntityManager()) {

            // Conexión y búsqueda por clave primaria
            Object base = em.createNativeQuery("SELECT DATABASE()").getSingleResult();
            System.out.println("ejercicio 1");
            System.out.println("Base de datos: " + base);

            Pelicula existente = em.find(Pelicula.class, 1);
            System.out.println("ID 1: "
                    + (existente != null ? existente.getTitulo() + " | " + existente.getTarifa() : "No existe"));

            Pelicula inexistente = em.find(Pelicula.class, 99999);
            System.out.println("ID 99999: " + (inexistente != null ? inexistente.getTitulo() : "No existe (null)"));

            Session sesion = em.unwrap(Session.class);

            // Filtros y parámetros (título por B, tarifa 0.99-2.99, idiomas en
            // lista)
            System.out.println("ejercicio 2");
            List<Pelicula> filtradas = sesion.createSelectionQuery("""
                    from Pelicula p
                    where p.titulo ilike :patron
                      and p.tarifa between :minima and :maxima
                      and p.idiomaId in (:idiomas)
                    order by p.titulo asc, p.id asc
                    """, Pelicula.class)
                    .setParameter("patron", "B%")
                    .setParameter("minima", new BigDecimal("0.99"))
                    .setParameter("maxima", new BigDecimal("2.99"))
                    .setParameterList("idiomas", List.of(1, 2))
                    .getResultList();
            System.out.println("Total encontradas: " + filtradas.size());
            filtradas.stream().limit(5)
                    .forEach(p -> System.out.println(p.getId() + " | " + p.getTitulo() + " | " + p.getTarifa()));

            // Paginación
            System.out.println("ejercicio 3");
            int tamanoPagina = 5;
            for (int numPagina = 1; numPagina <= 2; numPagina++) {
                int offset = (numPagina - 1) * tamanoPagina;
                List<Pelicula> pagina = sesion.createSelectionQuery("""
                        from Pelicula p
                        where p.titulo ilike :patron
                          and p.tarifa between :minima and :maxima
                          and p.idiomaId in (:idiomas)
                        order by p.titulo asc, p.id asc
                        """, Pelicula.class)
                        .setParameter("patron", "B%")
                        .setParameter("minima", new BigDecimal("0.99"))
                        .setParameter("maxima", new BigDecimal("2.99"))
                        .setParameterList("idiomas", List.of(1, 2))
                        .setFirstResult(offset)
                        .setMaxResults(tamanoPagina)
                        .getResultList();

                System.out.println("-- Página " + numPagina + " (offset=" + offset + ") --");
                if (pagina.isEmpty()) {
                    System.out.println("(Página vacía)");
                } else {
                    pagina.forEach(p -> System.out.println(p.getId() + " | " + p.getTitulo()));
                }
            }

            // Agrupación y proyección por idioma
            System.out.println("ejercicio 4");
            List<Tuple> resumen = sesion.createSelectionQuery("""
                    select i.nombre as idioma, count(p) as total, max(p.tarifa) as maxima
                    from Pelicula p
                    join p.idioma i
                    group by i.id, i.nombre
                    having count(p) >= :minimo
                    order by count(p) desc, i.id asc
                    """, Tuple.class)
                    .setParameter("minPeliculas", 2L)
                    .getResultList();

            for (Tuple fila : resumen) {
                System.out.println(fila.get("idioma", String.class) + " | Total: " +
                        fila.get("total", Long.class) + " | Tarifa máx: " +
                        fila.get("maxima", BigDecimal.class));
            }
        }
    }
}