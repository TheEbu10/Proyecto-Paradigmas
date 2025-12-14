package Persistencia;

import Dominio.Usuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RepositorioUsuarios {
    private static final String Directorio_Datos = "Sistema_Gestion_Contactos/Persistencia/Users/";
    private static final String ARCHIVO_USUARIOS = "usuarios.dat";
    private static final String RUTA_COMPLETA = Directorio_Datos + ARCHIVO_USUARIOS;

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
        guardarLista(usuarios);
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
            List<Usuario> usuarios = (List<Usuario>) ois.readObject();
            return usuarios;
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
}
