package Gl1tch_st0re.resenas.repository;

import Gl1tch_st0re.resenas.model.resenaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface resenaRepository extends JpaRepository<resenaModel, Long> {
    boolean existsByProductoIdAndUsuarioId(Long productoId, Long usuarioId);
    boolean existsByProductoIdAndUsuarioIdAndIdNot(Long productoId, Long usuarioId, Long id);
}