package Gl1tch_st0re.clientes.repository;

import Gl1tch_st0re.clientes.model.perfilClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface perfilClienteRepository extends JpaRepository<perfilClienteModel, Long> {
    boolean existsByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndIdNot(Long usuarioId, Long id);
}