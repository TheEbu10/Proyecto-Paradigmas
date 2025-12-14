package Dominio;

import Persistencia.RepositorioContactos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    if (s.getEstado() != SolicitudCompartir.EstadoSolicitud.PENDIENTE) throw new Exception("Solicitud ya procesada: estado actual " + s.getEstado().getDescripcion());

    List<Contacto> actuales = repo.cargarListaContactos(receptor.getNombreUsuario(), passwordReceptor);
    
    // 1. Crear un Map donde la clave es el teléfono y el valor es el objeto Contacto existente.
    Map<String, Contacto> contactosActualesPorTelefono = actuales.stream()
        .collect(Collectors.toMap(Contacto::getTelefono, c -> c));
        
    int contactosImportados = 0;
    List<String> mensajesDuplicados = new ArrayList<>(); 

    for (Contacto c : s.getContactosCompartidos()) {
        String telefonoIncoming = c.getTelefono();

        // 2. Verificar duplicado usando el Map
        if (!contactosActualesPorTelefono.containsKey(telefonoIncoming)) {
            // No es duplicado: se añade
            Contacto copia = new Contacto(c, s.getNombreSolicitante());
            actuales.add(copia);
            contactosImportados++;
        } else {
            // Es duplicado: obtener el contacto existente
            Contacto contactoExistente = contactosActualesPorTelefono.get(telefonoIncoming);
            
            // 3. Generar el mensaje detallado solicitado por el usuario
            String mensajeDetalle = String.format(
                " - Contacto enviado: '%s' | Ya registrado como: '%s' (Teléfono: %s)",
                c.getNombre(), // Nombre como lo envió el solicitante
                contactoExistente.getNombre(), // Nombre como lo tiene el receptor (¡Nuevo!)
                c.getTelefono()
            );
            mensajesDuplicados.add(mensajeDetalle);
        }
    }
    
    // Guardar lista actualizada y solicitud
    repo.guardarListaContactos(receptor.getNombreUsuario(), actuales, passwordReceptor);

    s.setEstado(SolicitudCompartir.EstadoSolicitud.ACEPTADA);
    repo.guardarSolicitud(s);
    
    // 4. Reportar duplicados lanzando una excepción con el mensaje completo de advertencia
    if (!mensajesDuplicados.isEmpty()) {
        StringBuilder mensajeFinal = new StringBuilder();
        mensajeFinal.append("ADVERTENCIA: Se importaron ").append(contactosImportados).append(" contactos a tu lista.\n");
        mensajeFinal.append("Los siguientes ").append(mensajesDuplicados.size()).append(" contactos no se importaron porque su número de teléfono ya existe en tu lista:\n");
        
        for (String detalle : mensajesDuplicados) {
            mensajeFinal.append(detalle).append("\n");
        }
        
        throw new Exception(mensajeFinal.toString()); 
    } 
}

    public List<SolicitudCompartir> verSolicitudesEnviadas(Usuario solicitante) {
        return repo.cargarSolicitudesEnviadas(solicitante.getNombreUsuario());
    }

    public void rechazarSolicitud(String idSolicitud, Usuario receptor) throws Exception {
    SolicitudCompartir s = repo.buscarSolicitudPorId(idSolicitud);
    
    if (s == null) {
        throw new Exception("Solicitud no encontrada.");
    }
    // Verificación de que la solicitud sea para el usuario logeado
    if (!s.getNombreDestinatario().equals(receptor.getNombreUsuario())) {
        throw new Exception("Solicitud no dirigida a este usuario.");
    }
    // Verificación de que la solicitud esté pendiente
    if (s.getEstado() != SolicitudCompartir.EstadoSolicitud.PENDIENTE) {
        throw new Exception("Solicitud ya procesada: estado actual " + s.getEstado().getDescripcion());
    }

    // 1. Cambiar el estado a RECHAZADA
    s.setEstado(SolicitudCompartir.EstadoSolicitud.RECHAZADA);
    
    // 2. Guardar la solicitud actualizada en el repositorio
    repo.guardarSolicitud(s); 
    }
   
}
