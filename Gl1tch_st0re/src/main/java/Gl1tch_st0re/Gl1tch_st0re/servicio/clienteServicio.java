package Gl1tch_st0re.Gl1tch_st0re.servicio;

import Gl1tch_st0re.Gl1tch_st0re.modelo.clienteModelo;
import Gl1tch_st0re.Gl1tch_st0re.repositorio.clienteRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class clienteServicio {
    @Autowired
    public clienteRepositorio clienteRepositorio;

public List<clienteModelo> findAll() {
        return clienteRepositorio.findAll();
    }


}
