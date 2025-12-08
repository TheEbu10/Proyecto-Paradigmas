package Dominio;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SolicitudCompartir implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EstadoSolicitud {
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
    private final List<Contacto> contactosCompartidos;

    private static String generarIdCorta() {
        int numero = (int) (Math.random() * 10000);
        return String.format("%04d", numero);
    }

    public SolicitudCompartir(String nombreSolicitante, String nombreDestinatario, List<Contacto> contactosCompartidos) {
        this.idSolicitud = generarIdCorta();
        this.nombreSolicitante = nombreSolicitante;
        this.nombreDestinatario = nombreDestinatario;
        this.fechaSolicitud = new Date();
        this.estado = EstadoSolicitud.PENDIENTE;
        this.contactosCompartidos = contactosCompartidos;
    }

    public String getIdSolicitud() { return idSolicitud; }
    public String getNombreSolicitante() { return nombreSolicitante; }
    public String getNombreDestinatario() { return nombreDestinatario; }
    public Date getFechaSolicitud() { return fechaSolicitud; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud nuevoEstado) { this.estado = nuevoEstado; }
    public List<Contacto> getContactosCompartidos() { return contactosCompartidos; }

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
