package todolist.service;

import todolist.dto.UsuarioData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest // Carga el contexto completo de Spring para pruebas de integración
@Sql(scripts = "/clean-db.sql") // Limpia la base de datos antes de cada test
public class UserListServiceTest {

    @MockBean
    private UsuarioService usuarioService; // Servicio simulado (mock) para evitar conexión real a BD

    @Test
    public void testFindAllUsuariosReturnsAllUsers() {
        // Crea dos objetos mock de UsuarioData
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("user1@umh.es");

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("user2@umh.es");

        // Simula la respuesta del servicio con una lista de 2 usuarios
        when(usuarioService.findAllUsuarios()).thenReturn(Arrays.asList(usuario1, usuario2));

        // Llama al método y verifica que devuelve los 2 usuarios
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        assertThat(usuarios).hasSize(2);
    }

    @Test
    public void testFindAllUsuariosWithNoUsers() {
        // Simula la respuesta del servicio con lista vacía
        when(usuarioService.findAllUsuarios()).thenReturn(Collections.emptyList());

        // Llama al método y verifica que la lista está vacía
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        assertThat(usuarios).isEmpty();
    }

    @Test
    public void testFindAllUsuariosConvertsToDTO() {
        // Crea un usuario simulado con ID y email
        UsuarioData usuarioMock = new UsuarioData();
        usuarioMock.setId(1L);
        usuarioMock.setEmail("dto-test@umh.es");

        // Simula que el servicio devuelve solo este usuario
        when(usuarioService.findAllUsuarios()).thenReturn(Collections.singletonList(usuarioMock));

        // Verifica que el DTO recibido no expone la contraseña
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        UsuarioData retrievedUser = usuarios.get(0);

        assertThat(retrievedUser.getPassword()).isNull();
        assertThat(retrievedUser.getEmail()).isEqualTo("dto-test@umh.es");
    }
}
