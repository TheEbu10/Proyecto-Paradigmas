package Dominio;

import Persistencia.RepositorioUsuarios;
import Persistencia.UtileriaSeguridad;

public class ServicioUsuarios {

    private final RepositorioUsuarios repo = new RepositorioUsuarios();

    public void registrarUsuario(String nombreCompleto, String nombreUsuario, String password, String email) throws Exception {
        if (repo.buscarPorNombreUsuario(nombreUsuario) != null) {
            throw new Exception("El nombre de usuario ya existe");
        }
        
        // 2. Verificar unicidad de Correo Electrónico
        if (repo.buscarPorEmail(email) != null) {
            throw new Exception("El correo electrónico en uso");
        }
        
        // Si ambas verificaciones pasan, se procede al registro
        Usuario u = new Usuario(nombreCompleto, nombreUsuario, password, email);
        repo.guardar(u);
    }

    public Usuario autenticar(String nombreUsuario, String password) throws Exception {
        Usuario u = repo.buscarPorNombreUsuario(nombreUsuario);
        if (u == null) throw new Exception("Usuario no encontrado");
        boolean valido = UtileriaSeguridad.validarHash(password, u.getPasswordhash());
        if (!valido) throw new Exception("Contraseña inválidas");
        return u;
    }

    
}
