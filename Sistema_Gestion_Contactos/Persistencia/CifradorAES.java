package Persistencia;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Base64;

public class CifradorAES {
    
    private static final String ALGORITMO = "AES";

    public static String cifrar(String datos, Key llave) throws Exception {
        // Inicializa el objeto Cipher con el algoritmo AES
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        // Configura el Cipher en modo ENCRYPT con la llave
        cipher.init(Cipher.ENCRYPT_MODE, llave);
        
        // Cifra los bytes de la cadena de entrada
        byte[] datosCifrados = cipher.doFinal(datos.getBytes());
        
        // Codifica el resultado binario cifrado a Base64 para que pueda ser guardado en un archivo de texto
        return Base64.getEncoder().encodeToString(datosCifrados);
    }
    
    public static String descifrar(String datosCifradosBase64, Key llave) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, llave);
        
        byte[] datosDecodificados = Base64.getDecoder().decode(datosCifradosBase64);
        
        byte[] datosDescifrados = cipher.doFinal(datosDecodificados);
        
        return new String(datosDescifrados);
    }
}
