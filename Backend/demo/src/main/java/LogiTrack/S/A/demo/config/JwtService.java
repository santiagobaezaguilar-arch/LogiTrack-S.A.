package LogiTrack.S.A.demo.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    /*
     * Clave secreta para firmar el token.
     * En producción esto va en variables de entorno, no en el código.
     * Para el proyecto educativo lo dejamos aquí igual que en poscampus.
     */
    private final String SECRET = "logitrack_clave_super_secreta_2026_sa";

    // 8 horas — suficiente para una jornada laboral
    private final long EXPIRATION = 1000L * 60 * 60 * 8;

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /*
     * Genera el JWT con el correo del usuario como subject.
     * El correo es el identificador único que usamos en toda la app.
     */
    public String generateToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     * Valida el token y devuelve el correo (subject).
     * Devuelve null si el token está vencido, fue alterado o la firma no coincide.
     */
    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
