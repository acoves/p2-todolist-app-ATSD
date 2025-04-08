package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import todolist.service.UsuarioService;
import todolist.authentication.ManagerUserSession;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitario para verificar la lógica del endpoint que permite a un administrador
 * bloquear o desbloquear usuarios desde la lista de usuarios.
 */
@WebMvcTest(UsuarioController.class)
class AdminUserBlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    void updateUserStatus_whenAdminBlocksUser_shouldReturnOk() throws Exception {
        // Simula que el usuario logeado es un admin con ID 2
        when(managerUserSession.usuarioLogeado()).thenReturn(2L);

        // Intenta bloquear al usuario con ID 1
        mockMvc.perform(put("/registered/1/status?enabled=false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verifica que el servicio se llamó con los argumentos correctos
        verify(usuarioService).toggleUserStatus(1L, false);
    }

    @Test
    void updateUserStatus_whenAdminBlocksSelf_shouldReturnForbidden() throws Exception {
        // Simula que el admin intenta bloquearse a sí mismo
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        mockMvc.perform(put("/registered/1/status?enabled=false"))
                .andExpect(status().isForbidden());

        // Verifica que no se llamó al servicio
        verify(usuarioService, never()).toggleUserStatus(any(), anyBoolean());
    }

    @Test
    void updateUserStatus_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        // Simula que no hay ningún usuario autenticado
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(put("/registered/1/status?enabled=false"))
                .andExpect(status().isUnauthorized());
    }
}