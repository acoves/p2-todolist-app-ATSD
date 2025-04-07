package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class UserListWebTest {

    @Autowired
    private MockMvc mockMvc; // Utilidad para simular peticiones HTTP a los controladores

    @MockBean
    private UsuarioService usuarioService; // Servicio simulado para evitar llamadas reales a la BD

    @MockBean
    private ManagerUserSession managerUserSession; // Simulación del control de sesión de usuario

    // Comprueba que si no hay usuario logeado, se redirige al login
    @Test
    public void testUserListRequiresAuthentication() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/registered"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // Comprueba que si hay usuario logeado, se muestra correctamente la lista de usuarios
    @Test
    public void testUserListShowsDataWhenAuthenticated() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        // Mock del usuario logeado
        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        loggedUser.setNombre("Richard Stallman");
        loggedUser.setEmail("richard");

        when(usuarioService.findById(1L)).thenReturn(loggedUser);

        // Mock de otro usuario
        UsuarioData usuario2 = new UsuarioData();
        usuario2.setId(2L);
        usuario2.setEmail("linus");

        // Simula la respuesta de la lista de usuarios
        when(usuarioService.findAllUsuarios()).thenReturn(Arrays.asList(loggedUser, usuario2));

        // Verifica que se carga correctamente la vista y contiene el email del primer usuario
        mockMvc.perform(get("/registered"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//td[text()='richard']").exists());

        // Verifica que ambos usuarios aparecen en la tabla
        mockMvc.perform(get("/registered"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body//table/tbody/tr[1]/td[2][text()='richard']").exists())
                .andExpect(xpath("/html/body//table/tbody/tr[2]/td[2][text()='linus']").exists());
    }

    // Verifica que el campo contraseña no se muestra en la vista
    @Test
    public void testUserListDoesNotShowPasswords() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(loggedUser);

        // Usuario con contraseña (simulada)
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("test@umh.es");
        usuario.setPassword("1234");

        when(usuarioService.findAllUsuarios()).thenReturn(Collections.singletonList(usuario));

        // Verifica que la contraseña no aparece en el HTML generado
        mockMvc.perform(get("/registered"))
                .andExpect(content().string(not(containsString("1234"))));
    }

    // Verifica que se muestra un mensaje cuando no hay usuarios registrados
    @Test
    public void testUserListShowsEmptyMessage() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(loggedUser);

        // Simula que no hay usuarios
        when(usuarioService.findAllUsuarios()).thenReturn(Collections.emptyList());

        // Verifica que se muestra el mensaje de lista vacía
        mockMvc.perform(get("/registered"))
                .andExpect(content().string(containsString("No hay usuarios registrados")));
    }

    // Verifica que el identificador del usuario aparece correctamente
    @Test
    public void testUserListShowsIdsCorrectly() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(loggedUser);

        // Usuario con ID 99
        UsuarioData usuario = new UsuarioData();
        usuario.setId(99L);
        usuario.setEmail("test-id@umh.es");

        when(usuarioService.findAllUsuarios()).thenReturn(Collections.singletonList(usuario));

        // Verifica que el ID se muestra correctamente en la tabla
        mockMvc.perform(get("/registered"))
                .andExpect(content().string(containsString("99")));
    }
}
