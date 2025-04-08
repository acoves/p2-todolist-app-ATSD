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

/**
 * Pruebas de integración para la vista que muestra el listado de usuarios (/registered).
 * Estas pruebas simulan peticiones web usando MockMvc y validan el comportamiento del controlador.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserListWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    /**
     * Si no hay un usuario autenticado, se debe redirigir automáticamente a la página de login.
     */
    @Test
    public void testUserListRequiresAuthentication() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/registered"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Verifica que se muestra la lista de usuarios correctamente si el usuario está autenticado.
     * También comprueba que el HTML generado contiene los correos de los usuarios.
     */
    @Test
    public void testUserListShowsDataWhenAuthenticated() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);


        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        loggedUser.setNombre("Richard Stallman");
        loggedUser.setEmail("richard");

        when(usuarioService.findById(1L)).thenReturn(loggedUser);


        UsuarioData usuario2 = new UsuarioData();
        usuario2.setId(2L);
        usuario2.setEmail("linus");

        when(usuarioService.findAllUsuarios()).thenReturn(Arrays.asList(loggedUser, usuario2));

        mockMvc.perform(get("/registered"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//td[text()='richard']").exists());

        mockMvc.perform(get("/registered"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body//table/tbody/tr[1]/td[2][text()='richard']").exists())
                .andExpect(xpath("/html/body//table/tbody/tr[2]/td[2][text()='linus']").exists());
    }

    /**
     * Verifica que la contraseña del usuario no aparece en el HTML generado, garantizando privacidad.
     */
    @Test
    public void testUserListDoesNotShowPasswords() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(loggedUser);

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("test@umh.es");
        usuario.setPassword("1234");

        when(usuarioService.findAllUsuarios()).thenReturn(Collections.singletonList(usuario));

        mockMvc.perform(get("/registered"))
                .andExpect(content().string(not(containsString("1234"))));
    }

    /**
     * Verifica que si no hay usuarios registrados, se muestre el mensaje correspondiente en pantalla.
     */
    @Test
    public void testUserListShowsEmptyMessage() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(loggedUser);

        when(usuarioService.findAllUsuarios()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/registered"))
                .andExpect(content().string(containsString("No hay usuarios registrados")));
    }

    /**
     * Verifica que los IDs de usuario aparecen correctamente en la tabla HTML.
     */
    @Test
    public void testUserListShowsIdsCorrectly() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        UsuarioData loggedUser = new UsuarioData();
        loggedUser.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(loggedUser);

        UsuarioData usuario = new UsuarioData();
        usuario.setId(99L);
        usuario.setEmail("test-id@umh.es");

        when(usuarioService.findAllUsuarios()).thenReturn(Collections.singletonList(usuario));

        mockMvc.perform(get("/registered"))
                .andExpect(content().string(containsString("99")));
    }
}