package Gl1tch_st0re.inventario.repository;

import Gl1tch_st0re.inventario.model.inventarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface inventarioRepository extends JpaRepository<inventarioModel, Long> {
    boolean existsByProductoId(Long productoId);
    boolean existsByProductoIdAndIdNot(Long productoId, Long id);
}