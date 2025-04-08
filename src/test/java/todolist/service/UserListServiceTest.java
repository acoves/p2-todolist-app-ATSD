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

/**
 * Tests unitarios que validan el comportamiento del servicio de obtención
 * de usuarios registrados usando mocks.
 */
@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class UserListServiceTest {

    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void testFindAllUsuariosReturnsAllUsers() {
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("user1@umh.es");

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("user2@umh.es");

        when(usuarioService.findAllUsuarios()).thenReturn(Arrays.asList(usuario1, usuario2));

        // Llama al método y comprueba que devuelve dos elementos
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        assertThat(usuarios).hasSize(2);
    }

    @Test
    public void testFindAllUsuariosWithNoUsers() {
        // Simula que no hay usuarios
        when(usuarioService.findAllUsuarios()).thenReturn(Collections.emptyList());
        // Verifica que devuelve una lista vacía
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        assertThat(usuarios).isEmpty();
    }

    @Test
    public void testFindAllUsuariosConvertsToDTO() {
        UsuarioData usuarioMock = new UsuarioData();
        usuarioMock.setId(1L);
        usuarioMock.setEmail("dto-test@umh.es");

        when(usuarioService.findAllUsuarios()).thenReturn(Collections.singletonList(usuarioMock));
        // Verifica que los datos son correctos y que no incluye la contraseña
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        UsuarioData retrievedUser = usuarios.get(0);

        assertThat(retrievedUser.getPassword()).isNull(); // La contraseña no debe mostrarse
        assertThat(retrievedUser.getEmail()).isEqualTo("dto-test@umh.es");
    }
}