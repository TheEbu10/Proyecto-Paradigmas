// Solicitud de compartir contactos 
import java.util.Date;


public class SolicitudCompartir {
    private String nombreSolicitante;
    private String nombreReceptor;
    private String estadoSolicitud; 
    private Date fechaSolicitud;

    public SolicitudCompartir(String nombreSolicitante, String nombreReceptor) { // Constructor de la solicitud 
        this.nombreSolicitante = nombreSolicitante;
        this.nombreReceptor = nombreReceptor;
        this.estadoSolicitud = "Pendiente"; // Estado inicial
        this.fechaSolicitud = new Date(); 
    }

    public String getNombreSolicitante() { // obtencion de datos para la solicitud 
        return nombreSolicitante;
    }
    public String getNombreReceptor() {
        return nombreReceptor;
    }
    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) { // Modificacion del estado de la solicitud 
        this.estadoSolicitud = estadoSolicitud;
    }

    @Override
    public String toString() {
        return "SolicitudCompartir{" +
                "nombreSolicitante='" + nombreSolicitante + '\'' +
                ", nombreReceptor='" + nombreReceptor + '\'' +
                ", estadoSolicitud='" + estadoSolicitud + '\'' +
                ", fechaSolicitud=" + fechaSolicitud +
                '}';
    }
}