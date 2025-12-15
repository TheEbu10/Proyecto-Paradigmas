package Persistencia;

import Dominio.Usuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.security.Key;;

public class RepositorioUsuarios {
    private static final String CLAVE_SECRETA_SISTEMA = "AES"; 
    private static final Key LLAVE_SISTEMA;

    private static final String Directorio_Datos = "Sistema_Gestion_Contactos/Persistencia/Users/";
    private static final String ARCHIVO_USUARIOS = "usuarios.dat";
    private static final String RUTA_COMPLETA = Directorio_Datos + ARCHIVO_USUARIOS;

    static {
        try {
            LLAVE_SISTEMA = UtileriaSeguridad.derivarLlave(CLAVE_SECRETA_SISTEMA);
        } catch (Exception e) {
            // Manejo de error si no se puede generar la clave.
            throw new RuntimeException("ERROR: No se pudo inicializar la llave de cifrado del sistema.", e);
        }
    }
    public void guardar(Usuario usuario) {
        List<Usuario> usuarios = cargarTodos();

        // Evitar duplicados por nombreUsuario
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(usuario.getNombreUsuario())) {
                System.out.println("El usuario ya existe: " + usuario.getNombreUsuario());
                return;
            }
        }

        usuarios.add(usuario);
        List<Usuario> usuariosCifrados = cifrarLista(usuarios);
        guardarLista(usuariosCifrados);
        
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        List<Usuario> usuarios = cargarTodos();

        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return u;
            }
        }
        return null; //No fue encontrado el usuario
    }

    private List<Usuario> cargarTodos() {
       File archivo = new File(RUTA_COMPLETA);

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            @SuppressWarnings("unchecked")
            List<Usuario> usuariosCifrados = (List<Usuario>) ois.readObject();
            
            // Descifrar la lista antes de devolverla
            return descifrarLista(usuariosCifrados);
        } catch (Exception e) {
            System.out.println("Error cargando usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void guardarLista(List<Usuario> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_COMPLETA))) {
            oos.writeObject(usuarios);
        } catch (Exception e) {
            System.out.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public Usuario buscarPorEmail(String email) {
        List<Usuario> usuarios = cargarTodos();

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null; 
    }

    private List<Usuario> cifrarLista(List<Usuario> usuarios) {
        List<Usuario> listaCifrada = new ArrayList<>();
        try {
            for (Usuario u : usuarios) {
                // El constructor de deserialización que creamos en el paso 1 es perfecto para esto
                Usuario copiaCifrada = new Usuario(
                    CifradorAES.cifrar(u.getNombreCompleto(), LLAVE_SISTEMA),
                    u.getNombreUsuario(), // nombreUsuario no se cifra, es el ID de búsqueda
                    u.getPasswordhash(), // passwordHash no se cifra (ya es un hash)
                    CifradorAES.cifrar(u.getEmail(), LLAVE_SISTEMA),
                    true // Indica que se está usando el constructor de persistencia
                );
                listaCifrada.add(copiaCifrada);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error durante el cifrado de la lista de usuarios.", e);
        }
        return listaCifrada;
    }

    /** Descifra los campos sensibles (nombreCompleto, email) de una lista de Usuarios. */
    private List<Usuario> descifrarLista(List<Usuario> usuariosCifrados) {
        List<Usuario> listaDescifrada = new ArrayList<>();
        try {
            for (Usuario u : usuariosCifrados) {
                // Se necesitan descifrar los campos en el objeto cargado
                String nombreCompletoDescifrado = CifradorAES.descifrar(u.getNombreCompleto(), LLAVE_SISTEMA);
                String emailDescifrado = CifradorAES.descifrar(u.getEmail(), LLAVE_SISTEMA);
                
                // Reconstruir el objeto con los datos descifrados
                Usuario usuarioDescifrado = new Usuario(
                    nombreCompletoDescifrado,
                    u.getNombreUsuario(),
                    u.getPasswordhash(),
                    emailDescifrado,
                    true // Indica que se está usando el constructor de persistencia
                );
                listaDescifrada.add(usuarioDescifrado);
            }
        } catch (Exception e) {
            // Un error al descifrar significa que el archivo está corrupto o la clave es incorrecta
            System.err.println("ERROR al descifrar datos de usuario: El archivo podría estar corrupto o la clave secreta cambió. " + e.getMessage());
            return new ArrayList<>(); // Devuelve lista vacía para evitar errores
        }
        return listaDescifrada;
    }
}
