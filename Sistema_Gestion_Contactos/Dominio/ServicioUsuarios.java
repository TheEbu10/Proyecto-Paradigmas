package Dominio;

import Persistencia.RepositorioUsuarios;
import Persistencia.UtileriaSeguridad;

public class ServicioUsuarios {

    private final RepositorioUsuarios repo = new RepositorioUsuarios();

    public void registrarUsuario(String nombreCompleto, String nombreUsuario, String password, String email) throws Exception {
        if (repo.buscarPorNombreUsuario(nombreUsuario) != null) {
            throw new Exception("El usuario ya existe: " + nombreUsuario);
        }
        Usuario u = new Usuario(nombreCompleto, nombreUsuario, password, email);
        repo.guardar(u);
    }

    public Usuario autenticar(String nombreUsuario, String password) throws Exception {
        Usuario u = repo.buscarPorNombreUsuario(nombreUsuario);
        if (u == null) throw new Exception("Usuario no encontrado");
        boolean valido = UtileriaSeguridad.validarHash(password, u.getPasswordhash());
        if (!valido) throw new Exception("Credenciales inválidas");
        return u;
    }
}
