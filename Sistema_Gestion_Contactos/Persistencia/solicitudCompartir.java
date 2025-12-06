// Solicitud de compartir contactos 
public class solicitudCompartir {
    private String nombreSolicitante;
    private String nombreReceptor;
    private String estadoSolicitud; 

    public solicitudCompartir(String nombreSolicitante, String nombreReceptor) { // Constructor
        this.nombreSolicitante = nombreSolicitante;
        this.nombreReceptor = nombreReceptor;
        this.estadoSolicitud = "Pendiente"; // Estado inicial
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
}