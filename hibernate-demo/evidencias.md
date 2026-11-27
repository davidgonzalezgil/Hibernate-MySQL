# ACTIVIDAD 1
aulaPU es el nombre de la unidad de persistencia definida en persistence.xml que agrupa el proveedor JPA (HibernatePersistenceProvider), las entidades registradas (Pelicula, Idioma) y las propiedades de configuración. 
El esquema de MySQL (sakila) se indica al final de la cadena de conexión JDBC

# ACTIVIDAD 2
SELECT film_id, title, rental_rate 
FROM film 
WHERE title LIKE 'B%' 
  AND rental_rate BETWEEN 0.99 AND 2.99 
  AND language_id IN (1, 2) 
ORDER BY title ASC, film_id ASC;

# ACTIVIDAD 3
Con un tamaño de página de 5 elementos, la primera página tiene un desplazamiento de 0 y la segunda página tiene un desplazamiento de 5

# ACTIVIDAD 4
La cláusula WHERE filtra filas individuales antes de agruparlas, por lo que no puede evaluar funciones de agregación como count(p). 
La condición sobre el recuento total de películas por idioma debe ir en HAVING porque se evalúa después de haber formado los grupos con GROUP BY

# ACTIVIDAD 5
Alta: em.persist(nueva) pasa la entidad al estado gestionado y prepara la inserción; em.flush() sincroniza para obtener el ID autogenerado y tx.commit() confirma el INSERT en MySQL.

Setter confirmado: Al modificar el atributo de una entidad gestionada (p.setTarifa(...)) dentro de una transacción activa, Hibernate detecta el cambio automáticamente (dirty checking) y ejecuta el UPDATE al confirmar con tx.commit().

Prueba de rollback: em.flush() envía la sentencia UPDATE a la base de datos dentro de la transacción en curso, pero tx.rollback() aborta y deshace la transacción, dejando en MySQL el valor confirmado anteriormente (3.49).

Update HQL: sesion.createMutationQuery(...).executeUpdate() ejecuta el UPDATE directamente en la base de datos sin cargar ni pasar por las entidades en memoria, confirmándose con tx.commit()