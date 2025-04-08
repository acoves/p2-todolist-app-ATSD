package todolist.service;

import todolist.dto.TareaData;
import todolist.dto.UsuarioData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class TaskServiceTest {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    /**
     * Método auxiliar para registrar un usuario y asignarle dos tareas iniciales.
     * Devuelve un mapa con los IDs del usuario y de la primera tarea creada.
     */
    Map<String, Long> addUsuarioTareasBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("richard@umh.es");
        usuario.setPassword("1234");

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario);

        TareaData tarea1 = tareaService.nuevaTareaUsuario(usuarioNuevo.getId(), "Buy milk");
        tareaService.nuevaTareaUsuario(usuarioNuevo.getId(), "Book a flight");

        Map<String, Long> ids = new HashMap<>();
        ids.put("usuarioId", usuarioNuevo.getId());
        ids.put("tareaId", tarea1.getId());
        return ids;
    }
    /**
     * Verifica que una nueva tarea se puede crear correctamente para un usuario existente.
     */
    @Test
    public void testNuevaTareaUsuario() {


        Long usuarioId = addUsuarioTareasBD().get("usuarioId");


        TareaData nuevaTarea = tareaService.nuevaTareaUsuario(usuarioId, "Práctica 2");


        List<TareaData> tareas = tareaService.allTareasUsuario(usuarioId);

        assertThat(tareas).hasSize(3);
        assertThat(tareas).contains(nuevaTarea);
    }
    /**
     * Verifica que una tarea puede ser recuperada correctamente por su ID.
     */
    @Test
    public void testBuscarTarea() {


        Long tareaId = addUsuarioTareasBD().get("tareaId");



        TareaData lavarCoche = tareaService.findById(tareaId);


        assertThat(lavarCoche).isNotNull();
        assertThat(lavarCoche.getTitulo()).isEqualTo("Buy milk");
    }

    /**
     * Verifica que se puede modificar el título de una tarea correctamente.
     */
    @Test
    public void testModificarTarea() {


        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = ids.get("tareaId");



        tareaService.modificaTarea(tareaId, "Buy coffee");



        TareaData tareaBD = tareaService.findById(tareaId);
        assertThat(tareaBD.getTitulo()).isEqualTo("Buy coffee");

        List<TareaData> tareas = tareaService.allTareasUsuario(usuarioId);
        assertThat(tareas).contains(tareaBD);
    }

    /**
     * Verifica que se puede eliminar una tarea y que ya no es recuperable.
     */
    @Test
    public void testBorrarTarea() {


        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = ids.get("tareaId");


        tareaService.borraTarea(tareaId);

        assertThat(tareaService.findById(tareaId)).isNull();

        List<TareaData> tareas = tareaService.allTareasUsuario(usuarioId);
        assertThat(tareas).hasSize(1);
    }

    /**
     * Verifica que la tarea pertenece al usuario correspondiente.
     */
    @Test
    public void asignarEtiquetaATarea(){

        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = ids.get("tareaId");

        assertThat(tareaService.usuarioContieneTarea(usuarioId,tareaId)).isTrue();
    }

}