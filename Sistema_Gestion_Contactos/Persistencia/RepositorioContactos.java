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

    private static final String EXTENSION_CONTACTOS = ".dat";
    private static final String ARCHIVO_SOLICITUDES = "solicitudes.ser"; // Usaremos serialización para las solicitudes



    public void guardarListaContactos(String idUsuario, List<Contacto> contactos, String passwordUsuario) throws Exception {
        // 1. Derivar la llave AES a partir de la contraseña
        Key key = UtileriaSeguridad.derivarLlave(passwordUsuario);

        // 2. Serializar la lista de Contactos a bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(contactos);
        oos.close();
        
        // 3. Convertir bytes serializados a Base64 para cifrado
        String datosSerializadosBase64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        
        // 4. Cifrar la cadena Base64
        String datosCifrados = CifradorAES.cifrar(datosSerializadosBase64, key);

        // 5. Guardar el resultado cifrado en el archivo del usuario
        try (PrintWriter pw = new PrintWriter(new FileWriter(idUsuario + EXTENSION_CONTACTOS))) {
            pw.println(datosCifrados);
        }
    }


    public List<Contacto> cargarListaContactos(String idUsuario, String passwordUsuario) throws Exception {
        File archivo = new File(idUsuario + EXTENSION_CONTACTOS);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }

        // 1. Derivar la llave
        Key key = UtileriaSeguridad.derivarLlave(passwordUsuario);

        // 2. Leer los datos cifrados del archivo
        String datosCifrados;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            datosCifrados = br.readLine();
            if (datosCifrados == null || datosCifrados.isEmpty()) return new ArrayList<>();
        }
        
        // 3. Descifrar la cadena Base64
        String datosSerializadosBase64 = CifradorAES.descifrar(datosCifrados, key);
        
        // 4. Descodificar de Base64 y Deserializar la lista
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
        try (FileOutputStream fos = new FileOutputStream(ARCHIVO_SOLICITUDES);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(solicitudes);
        } catch (IOException e) {
            System.err.println("Error al guardar solicitudes: " + e.getMessage());
        }
    }
    
    public void guardarSolicitud(SolicitudCompartir nuevaSolicitud) {
        List<SolicitudCompartir> solicitudes = cargarTodasSolicitudes();
        
        // Intentar actualizar si existe (por ID), si no, añadir
        int index = -1;
        for (int i = 0; i < solicitudes.size(); i++) {
            if (solicitudes.get(i).getIdSolicitud().equals(nuevaSolicitud.getIdSolicitud())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            solicitudes.set(index, nuevaSolicitud); // Actualiza el estado
        } else {
            solicitudes.add(nuevaSolicitud); // Nueva solicitud
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
}
