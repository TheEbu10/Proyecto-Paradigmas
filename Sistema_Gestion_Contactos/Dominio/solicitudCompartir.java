package Dominio;
import java.util.Date;

public class SolicitudCompartir { // Clase para representar una solicitud de compartir contactos
    public enum EstadoSolicitud { // estado de la solicitud 
        PENDIENTE("Pendiente"),
        ACEPTADA("Aceptada"),
        RECHAZADA("Rechazada"),
        CANCELADA("Cancelada");

        private final String descripcion;

        EstadoSolicitud(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    private final String idSolicitud;
    private final String nombreSolicitante;
    private final String nombreDestinatario;
    private final Date fechaSolicitud;
    private EstadoSolicitud estado;

    private static String generarIdCorta() { // Genera un id de 4 dígitos
        int numero = (int)(Math.random() * 10000); 
        return String.format("%04d", numero);       
    }

    public SolicitudCompartir(String nombreSolicitante, String nombreDestinatario) { // Constructor de la solicitud 
        this.idSolicitud = generarIdCorta();
        this.nombreSolicitante = nombreSolicitante;
        this.nombreDestinatario = nombreDestinatario;
        this.fechaSolicitud = new Date();
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public String getIdSolicitud() { 
        return idSolicitud; 
    }
    public String getNombreSolicitante() { 
        return nombreSolicitante;
    }
    public String getNombreDestinatario() { 
        return nombreDestinatario;
        }
    public Date getFechaSolicitud() { 
        return fechaSolicitud;
    }
    public EstadoSolicitud getEstado() { 
        return estado; 
        }

    public void setEstado(EstadoSolicitud nuevoEstado) {
        this.estado = nuevoEstado;
    }

    @Override
    public String toString() {
        return "SolicitudCompartir{" +
                "idSolicitud='" + idSolicitud + '\'' +
                ", nombreSolicitante='" + nombreSolicitante + '\'' +
                ", nombreDestinatario='" + nombreDestinatario + '\'' +
                ", fechaSolicitud=" + fechaSolicitud +
                ", estado=" + estado.getDescripcion() +
                '}';
    }
}
