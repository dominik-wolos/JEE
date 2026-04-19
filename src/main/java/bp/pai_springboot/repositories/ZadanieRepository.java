package bp.pai_springboot.repositories;

import bp.pai_springboot.entities.Zadanie;
import org.springframework.data.repository.CrudRepository;

public interface ZadanieRepository extends CrudRepository<Zadanie, Long> {

    Iterable<Zadanie> findByWykonane(boolean wykonane);

    Iterable<Zadanie> findByKosztLessThan(double koszt);

    Iterable<Zadanie> findByKosztBetween(double min, double max);
}
