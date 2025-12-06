package Dominio;

import Persistencia.UtileriaSeguridad;
import java.io.Serializable;
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nombreCompleto;
    private String nombreUsuario;
    private String passwordHash;
    private String email;

    public Usuario(String nombreCompleto, String nombreUsuario, String passwordHash, String email) {
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash;
        this.email = email;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNombreCompleto() {
        retunr nombreCompleto;
    }

    public String getpasswordhash() {
        return passwordHash;
    }

    public String getemail() {
        return email;
    }
