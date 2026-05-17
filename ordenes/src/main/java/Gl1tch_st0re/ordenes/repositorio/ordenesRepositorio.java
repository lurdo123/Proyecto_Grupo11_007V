package Gl1tch_st0re.ordenes.repositorio;

import Gl1tch_st0re.ordenes.modelo.ordenesModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ordenesRepositorio extends JpaRepository<ordenesModelo, Long> {
    List<ordenesModelo> findByUsuario(String usuario);
    List<ordenesModelo> findByEstado(String estado);
}