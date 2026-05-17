package Gl1tch_st0re.catalogo.repository;

import Gl1tch_st0re.catalogo.model.catalogoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface catalogoRepository extends JpaRepository<catalogoModel, Long> {
}