package Dominio;

import Persistencia.UtileriaSeguridad;
import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
// Atributos
    private String nombreCompleto;
    private String nombreUsuario;
    private String passwordHash;
    private String email;
//Constructores
   public Usuario(String nombreCompleto, String nombreUsuario, String passwordIngresada, String email) {
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        // La contraseña se hashea aquí
        this.passwordHash = UtileriaSeguridad.generarHash(passwordIngresada);
        this.email = (email == null) ? "" : email;
    }

    // Constructor auxiliar para DESERIALIZACIÓN
  
    public Usuario(String nombreCompleto, String nombreUsuario, String passwordHash, String email, boolean esParaPersistencia) {
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash; 
        this.email = (email == null) ? "" : email;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getPasswordhash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombreCompleto='" + nombreCompleto + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}











