package Gl1tch_st0re.compatibilidad.repositorio;

import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface compatibilidadRepositorio extends JpaRepository<compatibilidadModelo, Long> {
    Optional<compatibilidadModelo> findByComponenteBase(String componenteBase);
    boolean existsByComponenteBaseAndComponenteCompatible(String componenteBase, String componenteCompatible);
    boolean existsByComponenteBaseAndComponenteCompatibleAndIdNot(String componenteBase, String componenteCompatible, Long id);
}