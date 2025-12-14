package Dominio;

import Persistencia.RepositorioContactos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ServicioContactos {

    private final RepositorioContactos repo = new RepositorioContactos();

    public void agregarContacto(Usuario usuario, String passwordUsuario, Contacto nuevo) throws Exception {
        List<Contacto> lista = repo.cargarListaContactos(usuario.getNombreUsuario(), passwordUsuario);
        lista.add(nuevo);
        repo.guardarListaContactos(usuario.getNombreUsuario(), lista, passwordUsuario);
    }

    public List<Contacto> obtenerContactosOrdenados(Usuario usuario, String passwordUsuario) throws Exception {
        List<Contacto> lista = repo.cargarListaContactos(usuario.getNombreUsuario(), passwordUsuario);
        lista.sort(Comparator.comparing(Contacto::getNombre, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    public void solicitarCompartir(Usuario solicitante, String passwordSolicitante, String nombreReceptor) throws Exception {
        List<Contacto> contactosSolicitante = repo.cargarListaContactos(solicitante.getNombreUsuario(), passwordSolicitante);
        // Guardaremos la lista dentro de la Solicitud (no cifrada en este prototipo)
        SolicitudCompartir solicitud = new SolicitudCompartir(solicitante.getNombreUsuario(), nombreReceptor, new ArrayList<>(contactosSolicitante));
        repo.guardarSolicitud(solicitud);
    }

    public List<SolicitudCompartir> verSolicitudesPendientes(Usuario receptor) {
        return repo.cargarSolicitudesPendientes(receptor.getNombreUsuario());
    }

    public void aceptarSolicitud(String idSolicitud, Usuario receptor, String passwordReceptor) throws Exception {
        SolicitudCompartir s = repo.buscarSolicitudPorId(idSolicitud);
        if (s == null) throw new Exception("Solicitud no encontrada");
        if (!s.getNombreDestinatario().equals(receptor.getNombreUsuario())) throw new Exception("Solicitud no dirigida a este usuario");
        if (s.getEstado() != SolicitudCompartir.EstadoSolicitud.PENDIENTE) throw new Exception("Solicitud ya procesada");

       List<Contacto> actuales = repo.cargarListaContactos(receptor.getNombreUsuario(), passwordReceptor);
        
        // 1. Crear un Set para una búsqueda rápida de nombres de contactos ya existentes
        // Esto asume que el nombre es la clave para la duplicidad
        Set<String> nombresActuales = actuales.stream()
            .map(c -> c.getNombre().toLowerCase())
            .collect(Collectors.toSet()); // Necesitas importar java.util.stream.Collectors y java.util.Set
            
        int contactosImportados = 0;

        for (Contacto c : s.getContactosCompartidos()) {
            Contacto copia = new Contacto(c, s.getNombreSolicitante());
            
            // 2. Verificar duplicado ANTES de añadir (usando el nombre modificado)
            if (!nombresActuales.contains(copia.getNombre().toLowerCase())) {
                actuales.add(copia);
                contactosImportados++;
            } else {
                System.out.println("ADVERTENCIA: Contacto '" + copia.getNombre() + "' ya existe en la lista del receptor, se omitirá.");
            }
        }
        
        repo.guardarListaContactos(receptor.getNombreUsuario(), actuales, passwordReceptor);

        s.setEstado(SolicitudCompartir.EstadoSolicitud.ACEPTADA);
        repo.guardarSolicitud(s);
        
        System.out.println("Se importaron " + contactosImportados + " contactos.");
        
    }

    public List<SolicitudCompartir> verSolicitudesEnviadas(Usuario solicitante) {
        return repo.cargarSolicitudesEnviadas(solicitante.getNombreUsuario());
    }
}
