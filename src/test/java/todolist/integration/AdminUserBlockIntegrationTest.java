package todolist.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import todolist.model.Usuario;
import todolist.repository.UsuarioRepository;
import todolist.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración para validar el flujo completo de habilitar
 * y deshabilitar (bloquear/desbloquear) un usuario por parte del administrador.
 *
 * Este test utiliza tanto el repositorio como el servicio, y comprueba
 * que los cambios persisten correctamente en la base de datos.
 */
@SpringBootTest
class AdminUserBlockIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Este test simula todo el flujo real de bloqueo de un usuario desde que se registra
     * como habilitado, luego se bloquea, y finalmente se vuelve a habilitar.
     *
     * Se comprueba que:
     * 1. El estado inicial del usuario es "habilitado" (enabled = true)
     * 2. Tras llamar al servicio de toggleUserStatus con "false", el usuario queda bloqueado
     * 3. Se recupera de la base de datos y se confirma que está efectivamente bloqueado
     * 4. Luego se vuelve a habilitar usando toggleUserStatus con "true"
     * 5. Y se comprueba que el estado se ha actualizado correctamente en BD
     */
    @Test
    void fullAdminBlockWorkflow_shouldPersistBlockedStatus() {
        Usuario user = new Usuario("test@integration.com");
        user.setEnabled(true);
        usuarioRepository.save(user);

        usuarioService.toggleUserStatus(user.getId(), false);

        Usuario blockedUser = usuarioRepository.findById(user.getId()).get();
        assertFalse(blockedUser.isEnabled());

        usuarioService.toggleUserStatus(user.getId(), true);

        Usuario unblockedUser = usuarioRepository.findById(user.getId()).get();
        assertTrue(unblockedUser.isEnabled());
    }
}