package todolist.service;

import todolist.dto.UsuarioData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para comprobar el correcto funcionamiento de los métodos
 * principales del servicio de usuario: registro, login y consultas.
 */
@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class UserServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    // Método auxiliar para registrar un usuario y devolver su ID
    Long addUsuarioBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("richard@umh.es");
        usuario.setNombre("Richard Stallman");
        usuario.setPassword("1234");
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    @Test
    public void servicioLoginUsuario() {
        // Añade usuario válido a la BD
        addUsuarioBD();
        // Login correcto
        UsuarioService.LoginStatus loginStatus1 = usuarioService.login("richard@umh.es", "1234");

        // Contraseña incorrecta
        UsuarioService.LoginStatus loginStatus2 = usuarioService.login("richard@umh.es", "0000");
        // Usuario inexistente
        UsuarioService.LoginStatus loginStatus3 = usuarioService.login("ricardo.perez@gmail.com", "12345678");

        // Comprobaciones
        assertThat(loginStatus1).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);

        assertThat(loginStatus2).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);

        assertThat(loginStatus3).isEqualTo(UsuarioService.LoginStatus.USER_NOT_FOUND);
    }

    @Test
    public void servicioRegistroUsuario() {
        // Registro de nuevo usuario
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba2@gmail.com");
        usuario.setPassword("12345678");

        usuarioService.registrar(usuario);

        // Comprobación de existencia en la BD

        UsuarioData usuarioBaseDatos = usuarioService.findByEmail("usuario.prueba2@gmail.com");
        assertThat(usuarioBaseDatos).isNotNull();
        assertThat(usuarioBaseDatos.getEmail()).isEqualTo("usuario.prueba2@gmail.com");
    }

    @Test
    public void servicioRegistroUsuarioExcepcionConNullPassword() {
        // Registro sin contraseña (debe lanzar excepción)

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }


    @Test
    public void servicioRegistroUsuarioExcepcionConEmailRepetido() {

        // Registro duplicado (mismo email)
        addUsuarioBD();


        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("richard@umh.es");
        usuario.setPassword("12345678");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }

    @Test
    public void servicioRegistroUsuarioDevuelveUsuarioConId() {
        // Registro y comprobación de ID asignado

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");
        usuario.setPassword("12345678");

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario);


        assertThat(usuarioNuevo.getId()).isNotNull();

        // Verificación posterior
        UsuarioData usuarioBD = usuarioService.findById(usuarioNuevo.getId());
        assertThat(usuarioBD).isEqualTo(usuarioNuevo);
    }

    @Test
    public void servicioConsultaUsuarioDevuelveUsuario() {

        // Consulta de usuario existente
        Long usuarioId = addUsuarioBD();
        UsuarioData usuario = usuarioService.findByEmail("richard@umh.es");

        // Validaciones de los datos del usuario
        assertThat(usuario.getId()).isEqualTo(usuarioId);
        assertThat(usuario.getEmail()).isEqualTo("richard@umh.es");
        assertThat(usuario.getNombre()).isEqualTo("Richard Stallman");
    }
}