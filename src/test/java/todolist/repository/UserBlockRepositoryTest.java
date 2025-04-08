package todolist.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import todolist.model.Usuario;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para verificar el comportamiento del método findByEnabled()
 * del repositorio UsuarioRepository.
 */
@DataJpaTest
class UserBlockRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Verifica que el método findByEnabled(true) solo devuelve usuarios activos.
     */
    @Test
    void findByEnabled_shouldReturnOnlyActiveUsersWhenTrue() {
        // Given
        Usuario activeUser = new Usuario("active@test.com");
        activeUser.setEnabled(true);
        usuarioRepository.save(activeUser);

        Usuario blockedUser = new Usuario("blocked@test.com");
        blockedUser.setEnabled(false);
        usuarioRepository.save(blockedUser);

        // When
        List<Usuario> result = usuarioRepository.findByEnabled(true);

        // Then
        assertEquals(1, result.size());
        assertEquals("active@test.com", result.get(0).getEmail());
    }

    /**
     * Verifica que el método findByEnabled(false) solo devuelve usuarios bloqueados.
     */
    @Test
    void findByEnabled_shouldReturnOnlyBlockedUsersWhenFalse() {
        // Given
        Usuario blockedUser = new Usuario("blocked@test.com");
        blockedUser.setEnabled(false);
        usuarioRepository.save(blockedUser);

        // When
        List<Usuario> result = usuarioRepository.findByEnabled(false);

        // Then
        assertEquals(1, result.size());
        assertEquals("blocked@test.com", result.get(0).getEmail());
    }
}