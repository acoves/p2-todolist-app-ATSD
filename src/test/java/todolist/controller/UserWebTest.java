package todolist.controller;

import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para verificar el comportamiento del endpoint /login
 * desde la capa de controlador (web), usando MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc

public class UserWebTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UsuarioService usuarioService;

    /**
     * Verifica el caso exitoso de login:
     * cuando el usuario existe, la contraseña es correcta,
     * y se redirige a la página de tareas correspondiente.
     */
    @Test
    public void servicioLoginUsuarioOK() throws Exception {

        UsuarioData lauraGarcia = new UsuarioData();
        lauraGarcia.setNombre("Laura García");
        lauraGarcia.setId(1L);
        lauraGarcia.setEnabled(true);
        lauraGarcia.setAdmin(false);

        when(usuarioService.login("laura.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("laura.garcia@gmail.com"))
                .thenReturn(lauraGarcia);



        this.mockMvc.perform(post("/login")
                        .param("eMail", "laura.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    /**
     * Verifica que si el usuario no existe, el sistema devuelve un mensaje de error apropiado.
     */
    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {

        when(usuarioService.login("juan.luis@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);


        this.mockMvc.perform(post("/login")
                        .param("eMail","juan.luis@gmail.com")
                        .param("password","12345678"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    /**
     * Verifica que si la contraseña es incorrecta, se devuelve el mensaje de error adecuado.
     */
    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {

        when(usuarioService.login("laura.garcia@gmail.com", "000"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);


        this.mockMvc.perform(post("/login")
                        .param("eMail","laura.garcia@gmail.com")
                        .param("password","000"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }
}