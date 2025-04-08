package todolist.repository;


import todolist.model.Tarea;
import todolist.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class TaskTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TareaRepository tareaRepository;

    @Test
    public void crearTarea() {


        Usuario usuario = new Usuario("juan.fco@gmail.com");

        Tarea tarea = new Tarea(usuario, "Práctica 2");

        assertThat(tarea.getTitulo()).isEqualTo("Práctica 2");
        assertThat(tarea.getUsuario()).isEqualTo(usuario);
    }

    @Test
    public void laListaDeTareasDeUnUsuarioSeActualizaEnMemoriaConUnaNuevaTarea() {


        Usuario usuario = new Usuario("juan.fco@gmail.com");


        Set<Tarea> tareas = usuario.getTareas();
        Tarea tarea = new Tarea(usuario, "Práctica 2");

        assertThat(usuario.getTareas()).contains(tarea);
        assertThat(tareas).contains(tarea);
    }

    @Test
    public void comprobarIgualdadTareasSinId() {


        Usuario usuario = new Usuario("juan.fco@gmail.com");
        Tarea tarea1 = new Tarea(usuario, "Práctica 2");
        Tarea tarea2 = new Tarea(usuario, "Práctica 2");
        Tarea tarea3 = new Tarea(usuario, "Book a flight");

        assertThat(tarea1).isEqualTo(tarea2);
        assertThat(tarea1).isNotEqualTo(tarea3);
    }

    @Test
    public void comprobarIgualdadTareasConId() {


        Usuario usuario = new Usuario("juan.fco@gmail.com");
        Tarea tarea1 = new Tarea(usuario, "Práctica 2");
        Tarea tarea2 = new Tarea(usuario, "Buy milk");
        Tarea tarea3 = new Tarea(usuario, "Book a flight");
        tarea1.setId(1L);
        tarea2.setId(2L);
        tarea3.setId(1L);

        assertThat(tarea1).isEqualTo(tarea3);
        assertThat(tarea1).isNotEqualTo(tarea2);
    }



    @Test
    @Transactional
    public void guardarTareaEnBaseDatos() {


        Usuario usuario = new Usuario("richard@umh.es");
        usuarioRepository.save(usuario);
        // Se guarda una tarea asociada a ese usuario
        Tarea tarea = new Tarea(usuario, "Práctica 1");

        tareaRepository.save(tarea);


        // Verifica que se le ha asignado un ID
        assertThat(tarea.getId()).isNotNull();
        // Recupera y verifica que los datos se guardaron correctamente
        Tarea tareaBD = tareaRepository.findById(tarea.getId()).orElse(null);
        assertThat(tareaBD.getTitulo()).isEqualTo(tarea.getTitulo());
        assertThat(tareaBD.getUsuario()).isEqualTo(usuario);
    }

    @Test
    @Transactional
    public void salvarTareaEnBaseDatosConUsuarioNoBDLanzaExcepcion() {

        // Usuario no persistido
        Usuario usuario = new Usuario("juan.fco@gmail.com");
        Tarea tarea = new Tarea(usuario, "Práctica 2");


        // Al guardar la tarea, debe lanzar excepción
        Assertions.assertThrows(Exception.class, () -> {
            tareaRepository.save(tarea);
        });
    }

    @Test
    @Transactional
    public void unUsuarioTieneUnaListaDeTareas() {
        // Usuario con dos tareas persistidas
        Usuario usuario = new Usuario("richard@umh.es");
        usuarioRepository.save(usuario);
        Long usuarioId = usuario.getId();

        Tarea tarea1 = new Tarea(usuario, "Práctica 2");
        Tarea tarea2 = new Tarea(usuario, "Buy milk");
        tareaRepository.save(tarea1);
        tareaRepository.save(tarea2);

        // Se recupera el usuario y se verifica que tiene 2 tareas
        Usuario usuarioRecuperado = usuarioRepository.findById(usuarioId).orElse(null);


        assertThat(usuarioRecuperado.getTareas()).hasSize(2);
    }

    @Test
    @Transactional
    public void añadirUnaTareaAUnUsuarioEnBD() {
        // Crea y guarda el usuario
        Usuario usuario = new Usuario("richard@umh.es");
        usuarioRepository.save(usuario);
        Long usuarioId = usuario.getId();


        // Recupera el usuario y guarda una nueva tarea para él
        Usuario usuarioBD = usuarioRepository.findById(usuarioId).orElse(null);
        Tarea tarea = new Tarea(usuarioBD, "Práctica 2");
        tareaRepository.save(tarea);
        Long tareaId = tarea.getId();

        // Verifica que la tarea está asociada correctamente
        Tarea tareaBD = tareaRepository.findById(tareaId).orElse(null);
        assertThat(tareaBD).isEqualTo(tarea);
        assertThat(tarea.getUsuario()).isEqualTo(usuarioBD);
        // Verifica que el usuario tiene la tarea en su lista
        usuarioBD = usuarioRepository.findById(usuarioId).orElse(null);
        assertThat(usuarioBD.getTareas()).contains(tareaBD);
    }


    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // Crea usuario y tarea
        Usuario usuario = new Usuario("richard@umh.es");
        usuarioRepository.save(usuario);
        Tarea tarea = new Tarea(usuario, "Práctica 2");
        tareaRepository.save(tarea);

        // Recupera la tarea y la modifica
        Long tareaId = tarea.getId();
        tarea = tareaRepository.findById(tareaId).orElse(null);
        tarea.setTitulo("Esto es una prueba");

        // Verifica que el cambio se refleja en BD (por ser @Transactional)
        Tarea tareaBD = tareaRepository.findById(tareaId).orElse(null);
        assertThat(tareaBD.getTitulo()).isEqualTo(tarea.getTitulo());
    }
}