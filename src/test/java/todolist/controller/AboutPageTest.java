package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Test de integración para comprobar que la página "About" muestra
 * correctamente el nombre de la aplicación.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AboutPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAboutDevuelveNombreAplicacion() throws Exception {
        // Simula una petición GET a /about y verifica que el contenido contiene "ToDoList"
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("ToDoList")));
    }
}