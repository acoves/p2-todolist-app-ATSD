package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import todolist.service.UsuarioService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import todolist.dto.UsuarioData;

/**
 * Test de integración para verificar el comportamiento del login
 * de usuarios administradores y normales.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AdminLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void testLoginAdminRedirigeAListaUsuarios() throws Exception {
        // Crear y registrar un usuario con rol de administrador
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@umh.es");
        admin.setPassword("admin123");
        admin.setAdmin(true);
        usuarioService.registrar(admin);

        // Simular el login del admin y verificar redirección a la lista de usuarios
        mockMvc.perform(post("/login")
                        .param("eMail", "admin@umh.es")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registered")); // Redirige a la lista de usuarios
    }

    @Test
    public void testLoginUsuarioNormalRedirigeATareas() throws Exception {
        // Crear y registrar un usuario normal
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@umh.es");
        usuario.setPassword("user123");
        usuarioService.registrar(usuario);

        // Simular el login del usuario y verificar redirección a su lista de tareas
        mockMvc.perform(post("/login")
                        .param("eMail", "user@umh.es")
                        .param("password", "user123"))
                .andExpect(status().is3xxRedirection())// Espera redirección
                .andExpect(redirectedUrlPattern("/usuarios/*/tareas"));// Redirige a /usuarios/{id}/tareas
    }
}