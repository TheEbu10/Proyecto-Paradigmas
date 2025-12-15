package Persistencia;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class CifradorAES {

    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12; // 96 bits, recomendado para GCM
    private static final int TAG_LENGTH_BITS = 128;

    public static String cifrar(String datos, Key llave) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO);

        byte[] iv = new byte[IV_LENGTH_BYTES];
        SecureRandom rnd = new SecureRandom();
        rnd.nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
        cipher.init(Cipher.ENCRYPT_MODE, llave, spec);

        byte[] datosCifrados = cipher.doFinal(datos.getBytes(StandardCharsets.UTF_8));

        // Devuelve IV y ciphertext juntos, codificados en Base64 separados por ':'
        String ivB64 = Base64.getEncoder().encodeToString(iv);
        String ctB64 = Base64.getEncoder().encodeToString(datosCifrados);
        return ivB64 + ":" + ctB64;
    }

    public static String descifrar(String paqueteBase64, Key llave) throws Exception {
        // paquete tiene formato ivBase64:ciphertextBase64
        if (paqueteBase64 == null || !paqueteBase64.contains(":")) {
            throw new IllegalArgumentException("Formato de datos cifrados inválido");
        }

        String[] partes = paqueteBase64.split(":", 2);
        byte[] iv = Base64.getDecoder().decode(partes[0]);
        byte[] ct = Base64.getDecoder().decode(partes[1]);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
        cipher.init(Cipher.DECRYPT_MODE, llave, spec);

        byte[] datosDescifrados = cipher.doFinal(ct);
        return new String(datosDescifrados, StandardCharsets.UTF_8);
    }
}
