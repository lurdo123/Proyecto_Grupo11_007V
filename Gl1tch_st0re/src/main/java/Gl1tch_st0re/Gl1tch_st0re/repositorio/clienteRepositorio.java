package Gl1tch_st0re.Gl1tch_st0re.repositorio;

import Gl1tch_st0re.Gl1tch_st0re.modelo.clienteModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// import java.util.List;

@Repository
public interface clienteRepositorio extends JpaRepository<clienteModelo, Long> {

    // @Query("SELECT c FROM clienteModelo c WHERE c.nombre = :nombre")
    // List<clienteModelo> buscarPorNombre(@Param("nombre") String nombre);

    @Query(value = "SELECT * FROM CLIENTE WHERE APELLIDO = :apellido", nativeQuery = true)
    clienteModelo buscarPorApellido(@Param("apellido") String apellido);

    // @Query("SELECT c FROM clienteModelo c WHERE c.idCliente = :id")
    // java.util.Optional<clienteModelo> buscarPorId(@Param("id") Long id);
}