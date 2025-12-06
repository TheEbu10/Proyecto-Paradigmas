package dominio;

import java.io.Serializable;

public class Contacto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nombre;
    private String telefono;
    private String email;
    private String urlPaginaPersonal; 
    private String origenImportacion;

    public Contacto(String nombre, String telefono, String email, String urlPaginaPersonal) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.urlPaginaPersonal = urlPaginaPersonal;
        this.origenImportacion = null;
    }

    
     //Constructor para contactos importados de otro usuario
    public Contacto(Contacto original, String nombreUsuarioEmisor) {
        
        this(original.getNombre(), original.getTelefono(), original.getEmail(), original.getUrlPaginaPersonal());
        
        this.nombre = original.getNombre() + " (contacto de " + nombreUsuarioEmisor + ")";
        this.origenImportacion = nombreUsuarioEmisor;
    }

    // Getters y Setters
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getTelefono() { 
        return telefono; 
    }

    public void setTelefono(String telefono) { 
        this.telefono = telefono; 
    }

    public String getEmail() { 
        return email; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getUrlPaginaPersonal() { 
        return urlPaginaPersonal; 
    }
    public void setUrlPaginaPersonal(String urlPaginaPersonal) { 
        this.urlPaginaPersonal = urlPaginaPersonal; 
    }

    public String getOrigenImportacion() { return origenImportacion; }

    //Detalles de contacto
    public String toString() {
        String detalle = "Nombre: " + nombre + 
                         "\nTeléfono: " + telefono +
                         "\nEmail: " + email;
        
        if (urlPaginaPersonal != null && !urlPaginaPersonal.isEmpty()) {
            detalle += "\nURL Personal: " + urlPaginaPersonal;
        }
        if (origenImportacion != null && !origenImportacion.isEmpty()) {
            detalle += "\nNota: Importado de " + origenImportacion;
        }
        return detalle;
    }
}