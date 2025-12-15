package Persistencia;

import Dominio.Contacto;
import Dominio.SolicitudCompartir;
import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.stream.Collectors;

public class RepositorioContactos {

    private static final String DIRECTORIO_DATOS = "Sistema_Gestion_Contactos/Persistencia/Users";
    private static final String EXTENSION_CONTACTOS = ".dat";
    private static final String ARCHIVO_SOLICITUDES_NOMBRE = "solicitudes.ser";
    private static final String ARCHIVO_SOLICITUDES = DIRECTORIO_DATOS + File.separator + ARCHIVO_SOLICITUDES_NOMBRE;


    // metodo para asegurar que el directorio exista
    private void asegurarDirectorio() {
        File directorio = new File(DIRECTORIO_DATOS);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    public void guardarListaContactos(String idUsuario, List<Contacto> contactos, String passwordUsuario) throws Exception {
        asegurarDirectorio();
        
        // Derivar la llave AES a partir de la contraseña
        Key key = UtileriaSeguridad.derivarLlave(passwordUsuario);

        // Serializar la lista de Contactos a bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(contactos);
        oos.close();
        
        // Convertir bytes serializados a Base64 para cifrado
        String datosSerializadosBase64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        
        // Cifrar la cadena Base64
        String datosCifrados = CifradorAES.cifrar(datosSerializadosBase64, key);

        // guardar el resultado cifrado en el archivo del usuario
        String rutaArchivoUsuario = DIRECTORIO_DATOS + File.separator + idUsuario + EXTENSION_CONTACTOS;
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivoUsuario))) {
            pw.println(datosCifrados);
        }
    }

        // metodo para cargar la lista de contactos de un usuario
    public List<Contacto> cargarListaContactos(String idUsuario, String passwordUsuario) throws Exception {

        String rutaArchivoUsuario = DIRECTORIO_DATOS + File.separator + idUsuario + EXTENSION_CONTACTOS;
        
        File archivo = new File(rutaArchivoUsuario);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }

        // Derivar la llave 
        Key key = UtileriaSeguridad.derivarLlave(passwordUsuario);

        // Leer los datos cifrados del archivo
        String datosCifrados;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            datosCifrados = br.readLine();
            if (datosCifrados == null || datosCifrados.isEmpty()) return new ArrayList<>();
        }
        
        // Descifrar la cadena Base64
        String datosSerializadosBase64 = CifradorAES.descifrar(datosCifrados, key);
        
        // Descodificar de Base64 y Deserializar la lista
        byte[] datos = Base64.getDecoder().decode(datosSerializadosBase64);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(datos);
            ObjectInputStream ois = new ObjectInputStream(bis)) {
            
            @SuppressWarnings("unchecked")
            List<Contacto> contactos = (List<Contacto>) ois.readObject();
            return contactos;
        }
    }
    

    // Las solicitudes se guardarán en un archivo
    
    private List<SolicitudCompartir> cargarTodasSolicitudes() {
        File archivo = new File(ARCHIVO_SOLICITUDES);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(archivo);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            @SuppressWarnings("unchecked")
            List<SolicitudCompartir> solicitudes = (List<SolicitudCompartir>) ois.readObject();
            return solicitudes;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar solicitudes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void guardarTodasSolicitudes(List<SolicitudCompartir> solicitudes) {
        asegurarDirectorio();
        
        try (FileOutputStream fos = new FileOutputStream(ARCHIVO_SOLICITUDES);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(solicitudes);
        } catch (IOException e) {
            System.err.println("Error al guardar solicitudes: " + e.getMessage());
        }
    }
    
    public void guardarSolicitud(SolicitudCompartir nuevaSolicitud) {
        List<SolicitudCompartir> solicitudes = cargarTodasSolicitudes();
        
        int index = -1;
        for (int i = 0; i < solicitudes.size(); i++) {
            if (solicitudes.get(i).getIdSolicitud().equals(nuevaSolicitud.getIdSolicitud())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            solicitudes.set(index, nuevaSolicitud); 
        } else {
            solicitudes.add(nuevaSolicitud);
        }
        
        guardarTodasSolicitudes(solicitudes);
    }
    
    public List<SolicitudCompartir> cargarSolicitudesPendientes(String idUsuarioReceptor) {
        List<SolicitudCompartir> todas = cargarTodasSolicitudes();
        return todas.stream()
                .filter(s -> s.getNombreDestinatario().equals(idUsuarioReceptor) && 
                            s.getEstado() == SolicitudCompartir.EstadoSolicitud.PENDIENTE)
                .collect(Collectors.toList());
    }

    public SolicitudCompartir buscarSolicitudPorId(String idSolicitud) {
        return cargarTodasSolicitudes().stream()
                .filter(s -> s.getIdSolicitud().equals(idSolicitud))
                .findFirst()
                .orElse(null);
    }

    public List<SolicitudCompartir> cargarSolicitudesEnviadas(String idUsuarioSolicitante) {
        List<SolicitudCompartir> todas = cargarTodasSolicitudes();
        return todas.stream()
                .filter(s -> s.getNombreSolicitante().equals(idUsuarioSolicitante))
                .collect(Collectors.toList());
    }
}