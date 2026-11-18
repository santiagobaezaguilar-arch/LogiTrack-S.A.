package LogiTrack.S.A.demo.config;

import LogiTrack.S.A.demo.model.Credencial;
import LogiTrack.S.A.demo.repository.CredencialRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilter {

    private final JwtService jwtService;
    private final CredencialRepository credencialRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String header = http.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String correo = jwtService.validateToken(token);

            if (correo != null) {
                /*
                 * Aquí está la diferencia clave respecto a poscampus:
                 * en vez de lista vacía de roles, consultamos la BD
                 * para obtener el rol real del usuario (ADMIN o EMPLEADO).
                 * Así Spring Security puede evaluar @PreAuthorize correctamente.
                 */
                credencialRepository.findByUsuarioCorreo(correo).ifPresent(credencial -> {
                    // El rol se pasa con prefijo "ROLE_" que Spring Security exige
                    List<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + credencial.getRol().name())
                    );
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(correo, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        }

        chain.doFilter(request, response);
    }
}
