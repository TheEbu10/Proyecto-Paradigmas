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
    public Usuario(String nombreCompleto, String nombreUsuario, String passwordPlano, String email) {
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = UtileriaSeguridad.generarHash(passwordPlano);
        this.email = email;
    }
// Constructor auxiliar
    public Usuario(String nombreCompleto, String nombreUsuario, String passwordHash) {
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash;
        this.email = "";
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

    //Validar contraseña
    public boolean validarpassword(String passwordIngresada) {
        return UtilieriaSeguridad.validarHash(passwordIngresada, this.passwordHash);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombreCompleto='" + nombreCompleto + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                '}';
    }









