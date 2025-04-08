package todolist.repository;

import todolist.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByAdmin(boolean admin);

    List<Usuario> findByEnabled(boolean enabled);
}
