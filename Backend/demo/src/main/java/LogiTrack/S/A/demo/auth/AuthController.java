package LogiTrack.S.A.demo.auth;

import LogiTrack.S.A.demo.config.JwtService;
import LogiTrack.S.A.demo.exception.BusinessRuleException;
import LogiTrack.S.A.demo.mapper.CredencialMapper;
import LogiTrack.S.A.demo.mapper.UsuarioMapper;
import LogiTrack.S.A.demo.model.Credencial;
import LogiTrack.S.A.demo.model.Rol;
import LogiTrack.S.A.demo.model.Usuario;
import LogiTrack.S.A.demo.repository.CredencialRepository;
import LogiTrack.S.A.demo.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Login y registro de usuarios en el sistema LogiTrack")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final CredencialRepository credencialRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final CredencialMapper credencialMapper;

    // ── LOGIN ────────────────────────────────────────────────────────────────

    @Operation(
            summary = "Iniciar sesión",
            description = "Verifica las credenciales del usuario contra la base de datos. " +
                    "Devuelve un JWT válido por 8 horas y el rol para que el frontend adapte la UI."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso — token generado"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        // 1. Buscar el usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        // 2. Buscar sus credenciales
        Credencial credencial = credencialRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        // 3. Verificar contraseña
        //    Si la contraseña en BD es un hash BCrypt (empieza con $2a$/$2b$/$2y$),
        //    usa passwordEncoder.matches(). Si es texto plano, compara directo.
        //    Así funciona tanto con datos de prueba en texto plano como con
        //    contraseñas cifradas generadas por /auth/register.
        String storedPass = credencial.getPassword();
        boolean esHashBCrypt = storedPass != null && storedPass.startsWith("$2a$") || storedPass != null && storedPass.startsWith("$2b$") || storedPass != null && storedPass.startsWith("$2y$");

        if (esHashBCrypt) {
            if (!passwordEncoder.matches(request.password(), storedPass)) {
                throw new BusinessRuleException("Credenciales inválidas");
            }
        } else {
            if (!request.password().equals(storedPass)) {
                throw new BusinessRuleException("Credenciales inválidas");
            }
        }

        // 4. Generar JWT con el correo como subject
        String token = jwtService.generateToken(usuario.getCorreo());

        return ResponseEntity.ok(new LoginResponse(
                token,
                usuario.getCorreo(),
                usuario.getNombre(),
                credencial.getRol()
        ));
    }

    // ── REGISTRO ─────────────────────────────────────────────────────────────

    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un usuario nuevo con rol EMPLEADO automáticamente. " +
                    "El registro es público — cualquiera puede crear una cuenta, " +
                    "pero solo con permisos de empleado. Solo el ADMIN puede promover roles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Correo ya registrado o datos inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {

        // 1. Validar que el correo no exista ya
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new BusinessRuleException("El correo ya está registrado en el sistema");
        }

        // 2. Crear y guardar el usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setTelefono(request.telefono());
        usuario.setCorreo(request.correo());
        usuarioRepository.save(usuario);

        // 3. Crear credencial con contraseña cifrada y rol EMPLEADO fijo
        Credencial credencial = credencialMapper.buildEntity(
                usuario,
                passwordEncoder.encode(request.password()),
                Rol.EMPLEADO  // registro siempre crea EMPLEADO — el admin asigna roles después
        );
        credencialRepository.save(credencial);

        // 4. Generar JWT y devolver respuesta igual que en login
        String token = jwtService.generateToken(usuario.getCorreo());

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponse(
                token,
                usuario.getCorreo(),
                usuario.getNombre(),
                Rol.EMPLEADO
        ));
    }
}
