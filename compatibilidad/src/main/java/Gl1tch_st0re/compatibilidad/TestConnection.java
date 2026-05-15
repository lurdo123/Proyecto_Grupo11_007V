package Gl1tch_st0re.compatibilidad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestConnection implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public TestConnection(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        String result = jdbcTemplate.queryForObject("SELECT 'Conexión OK con MySQL :)' FROM dual", String.class);
        System.out.println(result);
    }
}