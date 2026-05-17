package Gl1tch_st0re.preventas.repositorio;

import Gl1tch_st0re.preventas.modelo.preventasModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface preventasRepositorio extends JpaRepository<preventasModelo, Long> {
    List<preventasModelo> findByUsuario(String usuario);
    List<preventasModelo> findByEstado(String estado);
}