package todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import todolist.authentication.ManagerUserSession;

@Controller
public class UsuarioController {
    // Servicio para acceder a los datos de usuarios
    private final UsuarioService usuarioService;
    // Componente para gestionar la sesión del usuario logeado
    private final ManagerUserSession managerUserSession;

    // Constructor con inyección de dependencias
    public UsuarioController(UsuarioService usuarioService, ManagerUserSession managerUserSession) {
        this.usuarioService = usuarioService;
        this.managerUserSession = managerUserSession;
    }
    // Mapea las peticiones GET a la URL "/registered"
    @GetMapping("/registered")
    public String listUsuarios(Model model) {
        // Comprueba si hay un usuario logeado en la sesión
        Long usuarioId = managerUserSession.usuarioLogeado();
        // Verificar autenticación
        boolean loggedIn = (usuarioId != null);
        model.addAttribute("loggedIn", loggedIn);

        if (!loggedIn) {
            return "redirect:/login";
        }

        UsuarioData usuario = usuarioService.findById(usuarioId);
        model.addAttribute("usuario", usuario);

        model.addAttribute("usuarios", usuarioService.findAllUsuarios());

        return "listaUsuarios";
    }
}