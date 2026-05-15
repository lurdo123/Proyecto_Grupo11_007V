package Gl1tch_st0re.autenticacion.repository;
import Gl1tch_st0re.autenticacion.model.autenticacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface autenticacionRepository extends JpaRepository<autenticacionModel, Long> {
Optional<autenticacionModel> findByUsuario(String usuario);
}
