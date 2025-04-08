package todolist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import todolist.model.Usuario;
import todolist.repository.UsuarioRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test unitarios para verificar el método toggleUserStatus()
 * que habilita o deshabilita a los usuarios.
 */
@ExtendWith(MockitoExtension.class)
class UserBlockServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void toggleUserStatus_shouldBlockUserWhenEnabledIsFalse() {
        // Simula usuario habilitado en BD
        Usuario user = new Usuario("test@example.com");
        user.setId(1L);
        user.setEnabled(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));

        usuarioService.toggleUserStatus(1L, false);

        assertFalse(user.isEnabled());
        verify(usuarioRepository).save(user);
    }

    @Test
    void toggleUserStatus_shouldUnblockUserWhenEnabledIsTrue() {
        // Simula usuario deshabilitado
        Usuario user = new Usuario("blocked@example.com");
        user.setId(2L);
        user.setEnabled(false);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(user));

        usuarioService.toggleUserStatus(2L, true);

        assertTrue(user.isEnabled());
        verify(usuarioRepository).save(user);
    }

    @Test
    void toggleUserStatus_shouldThrowExceptionForNonExistingUser() {
        // Simula que el usuario no existe
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UsuarioServiceException.class, () ->
                usuarioService.toggleUserStatus(99L, false)
        );
    }
}