package Gl1tch_st0re.pagos.repository;

import Gl1tch_st0re.pagos.model.pagoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface pagoRepository extends JpaRepository<pagoModel, Long> {
    boolean existsByIdTransaccionExterna(String idTransaccionExterna);
    boolean existsByIdTransaccionExternaAndIdNot(String idTransaccionExterna, Long id);
}