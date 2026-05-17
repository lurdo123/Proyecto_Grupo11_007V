package Gl1tch_st0re.envios.repositorio;

import Gl1tch_st0re.envios.modelo.enviosModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface enviosRepositorio extends JpaRepository<enviosModelo, Long> {
    List<enviosModelo> findByUsuario(String usuario);
    List<enviosModelo> findByEstado(String estado);
    List<enviosModelo> findByOrdenId(Long ordenId);
}