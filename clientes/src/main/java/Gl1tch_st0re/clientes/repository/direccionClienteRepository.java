package Gl1tch_st0re.clientes.repository;

import Gl1tch_st0re.clientes.model.direccionClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface direccionClienteRepository extends JpaRepository<direccionClienteModel, Long> {
    List<direccionClienteModel> findByClienteId(Long clienteId);
}