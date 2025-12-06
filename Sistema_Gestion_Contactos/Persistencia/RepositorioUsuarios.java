package Persistencia;

import Dominio.Usuario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

//Declaración de la clase y constante del archivo
public class RepositorioUsuarios {
    private static final String ARCHIVO_USUARIOS = "usuarios.dat";

//Guardar usuario
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

//Metodo buscar por nombre el usuario
public Usuario buscarPorNombreUsuario(String nombreUsuario) {
    List<Usuario> usuarios = cargarTodos();

    for (Usuario u : usuarios) {
        if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
            return u;
        }
    }
    return null; //No fue encontrado el usuario
}



//Metodo interno
//Metodo auxiliar cargarTodos
private List<Usuario> cargarTodos() {
    File archivo = new File(ARCHIVO_USUARIOS);

    if (!archivo.exists()) {
        return new ArrayList<>();
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
        return (List<Usuario>) ois.readObject();
    } catch (Exception e) {
        System.out.println("Error cargando usuarios: " + e.getMessage());
        return new ArrayList<>();
    }
}

//Metodo auxiliar guardarlista
private void guardarLista(List<Usuario> usuarios) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))) {
        oos.writeObject(usuarios);
    } catch (Exception e) {
        System.out.println("⚠ Error guardando usuarios: " + e.getMessage());
    }
}

