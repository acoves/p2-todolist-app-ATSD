package todolist.repository;

import todolist.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // Carga el contexto completo de Spring para ejecutar pruebas de integración
@Sql(scripts = "/clean-db.sql") // Limpia la base de datos antes de cada test con el script SQL
public class UserListTest {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio que se va a probar

    @Test
    public void testFindAllReturnsAllUsers() {
        // Crea el primer usuario y lo guarda en la base de datos
        Usuario usuario1 = new Usuario("repo-user1@umh.es");
        usuarioRepository.save(usuario1);

        // Crea el segundo usuario y lo guarda en la base de datos
        Usuario usuario2 = new Usuario("repo-user2@umh.es");
        usuarioRepository.save(usuario2);

        // Recupera todos los usuarios y comprueba que hay exactamente 2
        Iterable<Usuario> usuarios = usuarioRepository.findAll();
        assertThat(usuarios).hasSize(2);
    }
}
