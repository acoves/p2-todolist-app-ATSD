package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba para validar el comportamiento de la ruta /registered/:id cuando
 * se pasa un identificador inválido (no numérico).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserDescriptionWebTest {

    @Autowired
    private MockMvc mockMvc;
    /**
     * Este test verifica que si un usuario intenta acceder a una URL del tipo
     * /registered/abc (donde el id debería ser numérico), el sistema responde con
     * un código de estado 400 Bad Request.
     *
     * Esto asegura que el sistema maneja correctamente rutas mal formateadas
     * y evita que se procese una solicitud inválida que podría causar errores internos.
     */
    @Test
    public void testIdNoNumerico_EnUrl() throws Exception {
        mockMvc.perform(get("/registered/abc"))
                .andExpect(status().isBadRequest());
    }
}