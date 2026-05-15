package Gl1tch_st0re.garantias.repository;

import Gl1tch_st0re.garantias.model.garantiasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface garantiasRepository extends JpaRepository<garantiasModel, Long> {
}