package todolist.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todolist.dto.UsuarioData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests de integración para validar el registro de usuarios administradores,
 * garantizando que solo se pueda registrar un único admin.
 */
@SpringBootTest
@Transactional
public class AdminRegistrationTest {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Verifica que se puede registrar correctamente el primer administrador del sistema.
     */
    @Test
    public void testRegistrarPrimerAdmin() {

        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@umh.es");
        admin.setPassword("admin123");
        admin.setAdmin(true);

        usuarioService.registrar(admin);


        assertThat(usuarioService.existsByAdmin(true)).isTrue();
    }

    /**
     * Verifica que no se puede registrar un segundo administrador si ya existe uno.
     */
    @Test
    public void testRegistrarSegundoAdminFalla() {

        UsuarioData admin1 = new UsuarioData();
        admin1.setEmail("admin1@umh.es");
        admin1.setPassword("admin123");
        admin1.setAdmin(true);
        usuarioService.registrar(admin1);


        UsuarioData admin2 = new UsuarioData();
        admin2.setEmail("admin2@umh.es");
        admin2.setPassword("admin456");
        admin2.setAdmin(true);

        assertThrows(UsuarioServiceException.class, () -> usuarioService.registrar(admin2));
    }
}