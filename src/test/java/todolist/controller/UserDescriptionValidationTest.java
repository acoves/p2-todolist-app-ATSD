package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

/**
 * Test que valida la robustez del controlador cuando se recibe un ID con caracteres no numéricos.
 * Forma parte de las pruebas de seguridad y validación de entradas en URLs.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserDescriptionValidationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Este test simula una petición GET con una URL malformada donde el ID del usuario
     * contiene un carácter alfanumérico ("1a" en lugar de solo números).
     *
     * Se espera que el sistema detecte este error de formato y responda con un
     * código de estado 400 (Bad Request), evitando así posibles errores o ataques.
     */
    @Test
    public void testIdConCaracteresEspeciales() throws Exception {
        mockMvc.perform(get("/registered/1a"))
                .andExpect(status().isBadRequest());
    }
}