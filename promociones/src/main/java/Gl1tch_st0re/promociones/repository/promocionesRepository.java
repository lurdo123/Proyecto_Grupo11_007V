package Gl1tch_st0re.promociones.repository;

import Gl1tch_st0re.promociones.model.promocionesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface promocionesRepository extends JpaRepository<promocionesModel, Long> {
}