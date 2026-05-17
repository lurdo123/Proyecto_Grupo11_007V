package Gl1tch_st0re.envios;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class testConnection implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public testConnection(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        String result = jdbcTemplate.queryForObject("SELECT 'Conectado a MySQL para Envios :)' FROM dual", String.class);
        System.out.println(result);
    }
}