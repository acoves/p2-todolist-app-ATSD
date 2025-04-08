package todolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import todolist.dto.UsuarioData;
import org.springframework.web.bind.annotation.*;
import todolist.service.UsuarioService;
import todolist.authentication.ManagerUserSession;
import todolist.service.UsuarioServiceException;

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

    @PutMapping("/registered/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled
    ) {
        Long loggedUserId = managerUserSession.usuarioLogeado();

        // Verificar autenticación
        if (loggedUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            usuarioService.toggleUserStatus(id, enabled);
            return ResponseEntity.ok().build();
        } catch (UsuarioServiceException e) {
            return ResponseEntity.notFound().build();
        }
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

        return "usersList";
    }
    // Mapea las peticiones GET a la URL "/registered/{id}"
    @GetMapping("/registered/{id}")
    public String verUsuario(@PathVariable Long id, Model model) {
        Long usuarioLogeadoId = managerUserSession.usuarioLogeado();

        if (usuarioLogeadoId == null) {
            return "redirect:/login";
        }

        try {
            UsuarioData usuario = usuarioService.findById(id);
            model.addAttribute("loggedIn", true);
            model.addAttribute("usuarioLogeado", usuarioService.findById(usuarioLogeadoId));
            model.addAttribute("usuario", usuario);
            return "usersDescription";

        } catch (RuntimeException e) {
            return "redirect:/registered"; // Redirige si hay error
        }
    }
}