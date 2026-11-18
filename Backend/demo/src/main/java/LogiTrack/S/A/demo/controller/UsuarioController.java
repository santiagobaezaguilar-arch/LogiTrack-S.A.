package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.dto.request.UsuarioDtoRequest;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios — solo ADMIN")
@Validated
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /*
     * Solo el ADMIN puede crear, ver, editar y eliminar usuarios.
     * El registro público está en /auth/register (AuthController).
     */

    @Operation(summary = "Crear usuario (ADMIN)", description = "El admin crea un usuario con el rol que elija.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioDtoResponse> guardar(@Valid @RequestBody UsuarioDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(dto));
    }

    @Operation(summary = "Listar todos los usuarios (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioDtoResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @Operation(summary = "Buscar usuario por ID (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDtoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarId(id));
    }

    @Operation(summary = "Buscar usuario por correo (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buscar")
    public ResponseEntity<UsuarioDtoResponse> buscarCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(usuarioService.buscarCorreo(correo));
    }

    @Operation(summary = "Actualizar usuario (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDtoResponse> actualizar(
            @Valid @RequestBody UsuarioDtoRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.actualizar(dto, id));
    }

    @Operation(summary = "Eliminar usuario (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
