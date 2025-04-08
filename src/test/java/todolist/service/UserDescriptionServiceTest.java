package todolist.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import todolist.dto.UsuarioData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests para la funcionalidad de búsqueda de usuario por ID y manejo de datos inválidos
 */
@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserDescriptionServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void testBuscarUsuarioPorId_FormatoFechaInvalido() {
        // Crea un usuario con fecha de nacimiento mal formateada
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("fecha-invalida@umh.es");
        usuario.setPassword("1234");
        // Intenta asignar una fecha inválida: 31 de febrero no existe
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            usuario.setFechaNacimiento(sdf.parse("2019-02-31"));
        } catch (ParseException e) {

        }


        UsuarioData usuarioGuardado = usuarioService.registrar(usuario);
        UsuarioData usuarioEncontrado = usuarioService.findById(usuarioGuardado.getId());


        assertThat(usuarioEncontrado.getFechaNacimiento()).isNull();
    }

    @Test
    public void testBuscarUsuarioPorId_IdNegativo() {
        // Verifica que buscar un ID negativo lanza una excepción

        assertThrows(RuntimeException.class, () -> usuarioService.findById(-1L));
    }
}