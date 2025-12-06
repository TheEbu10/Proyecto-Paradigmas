// Solicitud de compartir contactos 
import java.util.Date;
import java.util.UUID;

public class SolicitudCompartir {
    public enum EstadoSolicitud { // Estado de la solicitud
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

    private final String idSolicitud; // Identificador único de la solicitud
    private final String nombreSolicitante;  
    private final String nombreDestinatario; 
    private final Date fechaSolicitud; 
    private EstadoSolicitud estado;    

    public SolicitudCompartir(String nombreSolicitante, String nombreDestinatario) {
        this.idSolicitud = UUID.randomUUID().toString(); 
        this.nombreSolicitante = nombreSolicitante;
        this.nombreDestinatario = nombreDestinatario;
        this.fechaSolicitud = new Date(); 
        this.estado = EstadoSolicitud.PENDIENTE; 
    }

    // Getters
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
    // Setters
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