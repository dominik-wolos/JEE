package bp.pai_springboot.repositories;

import bp.pai_springboot.entities.Zadanie;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for Zadanie entity.
 * Extends CrudRepository to provide basic CRUD operations.
 * Custom query methods use Spring Data JPA naming conventions.
 */
public interface ZadanieRepository extends CrudRepository<Zadanie, Long> {

    /**
     * Finds all tasks by completion status.
     * @param wykonane - true for completed tasks, false for incomplete
     * @return Iterable collection of matching tasks
     */
    Iterable<Zadanie> findByWykonane(boolean wykonane);

    /**
     * Finds all tasks with cost less than specified value.
     * @param koszt - maximum cost (exclusive)
     * @return Iterable collection of matching tasks
     */
    Iterable<Zadanie> findByKosztLessThan(double koszt);

    /**
     * Finds all tasks with cost between two values (inclusive).
     * @param min - minimum cost
     * @param max - maximum cost
     * @return Iterable collection of matching tasks
     */
    Iterable<Zadanie> findByKosztBetween(double min, double max);
}
