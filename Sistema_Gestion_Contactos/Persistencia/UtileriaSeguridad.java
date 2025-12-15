package Persistencia;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

public class UtileriaSeguridad {
    
    // Genera un hash
    public static String generarHash(String passwordPlano) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(passwordPlano.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash.", e);
        }
    }

    // Validación del hash
    public static boolean validarHash(String passwordPlano, String passwordHash) {
        String hashGenerado = generarHash(passwordPlano);
        return hashGenerado.equals(passwordHash);
    }
    
    // Deriva la llave para AES a partir de la contraseña del usuario
    public static Key derivarLlave(String passwordUsuario) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = md.digest(passwordUsuario.getBytes());
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al derivar llave.", e);
        }
    }

    // Deriva la llave maestra para cifrar datos globales (leer desde variable de entorno)
    public static Key derivarLlaveMaestra() {
        String master = System.getenv("SGC_MASTER_PASSWORD");
        if (master == null || master.isEmpty()) {
            throw new RuntimeException("La variable de entorno SGC_MASTER_PASSWORD no está definida. Defínela para habilitar cifrado de datos globales.");
        }
        return derivarLlave(master);
    }
}
