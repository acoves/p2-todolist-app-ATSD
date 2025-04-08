package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.TareaData;
import todolist.dto.UsuarioData;
import todolist.service.TareaService;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test de integración de la funcionalidad de tareas desde el punto de vista de la interfaz web.
 * Se prueban operaciones completas: listar, crear, editar y eliminar tareas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class TaskWebTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;


    @MockBean
    private ManagerUserSession managerUserSession;



    Map<String, Long> addUsuarioTareasBD() {

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("richard@umh.es");
        usuario.setPassword("1234");
        usuario = usuarioService.registrar(usuario);


        TareaData tarea1 = tareaService.nuevaTareaUsuario(usuario.getId(), "Buy milk");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Book a flight");

        Map<String, Long> ids = new HashMap<>();
        ids.put("usuarioId", usuario.getId());
        ids.put("tareaId", tarea1.getId());
        return ids;

    }

    /**
     * Test que verifica que la página de listado de tareas
     * muestra correctamente las tareas asociadas a un usuario autenticado.
     */
    @Test
    public void listaTareas() throws Exception {

        Long usuarioId = addUsuarioTareasBD().get("usuarioId");


        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);



        String url = "/usuarios/" + usuarioId.toString() + "/tareas";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("Buy milk"),
                        containsString("Book a flight")
                ))));
    }

    /**
     * Test que comprueba que al acceder a la ruta de crear nueva tarea,
     * se devuelve correctamente el formulario HTML con los datos esperados.
     */
    @Test
    public void getNuevaTareaDevuelveForm() throws Exception {

        Long usuarioId = addUsuarioTareasBD().get("usuarioId");


        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);



        String urlPeticion = "/usuarios/" + usuarioId.toString() + "/tareas/nueva";
        String urlAction = "action=\"/usuarios/" + usuarioId.toString() + "/tareas/nueva\"";

        this.mockMvc.perform(get(urlPeticion))
                .andExpect((content().string(allOf(
                        containsString("form method=\"post\""),
                        containsString(urlAction)
                ))));
    }

    /**
     * Test que simula la creación de una nueva tarea vía POST,
     * espera una redirección y luego comprueba que la tarea aparece listada.
     */
    @Test
    public void postNuevaTareaDevuelveRedirectYAñadeTarea() throws Exception {

        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);



        String urlPost = "/usuarios/" + usuarioId.toString() + "/tareas/nueva";
        String urlRedirect = "/usuarios/" + usuarioId.toString() + "/tareas";

        this.mockMvc.perform(post(urlPost)
                        .param("titulo", "Study"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));



        this.mockMvc.perform(get(urlRedirect))
                .andExpect((content().string(containsString("Study"))));
    }

    /**
     * Test que simula la creación de una nueva tarea vía POST,
     * espera una redirección y luego comprueba que la tarea aparece listada.
     */
    @Test
    public void deleteTareaDevuelveOKyBorraTarea() throws Exception {

        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaLavarCocheId = ids.get("tareaId");


        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);



        String urlDelete = "/tareas/" + tareaLavarCocheId.toString();

        this.mockMvc.perform(delete(urlDelete))
                .andExpect(status().isOk());


        String urlListado = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(urlListado))
                .andExpect(content().string(
                        allOf(not(containsString("Buy milk")),
                                containsString("Book a flight"))));
    }

    /**
     * Test que simula la edición de una tarea existente,
     * espera una redirección y luego comprueba que la tarea aparece listada.
     */
    @Test
    public void editarTareaActualizaLaTarea() throws Exception {

        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaLavarCocheId = ids.get("tareaId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);


        String urlEditar = "/tareas/" + tareaLavarCocheId + "/editar";
        String urlRedirect = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(post(urlEditar)
                        .param("titulo", "Buy coffee"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));



        String urlListado = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(urlListado))
                .andExpect(content().string(containsString("Buy coffee")));
    }
}