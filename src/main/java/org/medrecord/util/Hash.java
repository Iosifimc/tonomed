package org.medrecord.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase para generar hashes SHA-256 de las contraseñas
 */
public class Hash {

    // Constructor
    public Hash() {
    }

    // Método para generar hash SHA-256 (más seguro)
    public String sha256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al generar hash SHA-256: " + e.getMessage());
            return null;
        }
    }

    // Método para verificar contraseña
    public boolean verificarPassword(String passwordOriginal, String hashAlmacenado) {
        String hashGenerado = sha256(passwordOriginal);
        return hashGenerado != null && hashGenerado.equals(hashAlmacenado);
    }
}