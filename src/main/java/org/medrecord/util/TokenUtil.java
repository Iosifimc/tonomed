package org.medrecord.util;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

public class TokenUtil {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    // Generar token aleatorio de 32 bytes
    public static String generarToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    // Generar timestamp de expiración (24 horas desde ahora)
    public static Timestamp generarExpiracion() {
        LocalDateTime expiracion = LocalDateTime.now().plusHours(24);
        return Timestamp.valueOf(expiracion);
    }

    // Verificar si el token ha expirado
    public static boolean tokenExpirado(Timestamp expiracion) {
        if (expiracion == null) return true;
        return expiracion.before(new Timestamp(System.currentTimeMillis()));
    }
}