package UI;

import Dominio.Contacto;
import Dominio.SolicitudCompartir;
import Dominio.Usuario;
import Dominio.ServicioContactos;
import Dominio.ServicioUsuarios;
import java.util.List;
import java.util.Scanner;

public class InterfazUsuario {

    private final Scanner scanner = new Scanner(System.in);
    private final ServicioUsuarios servicioUsuarios = new ServicioUsuarios();
    private final ServicioContactos servicioContactos = new ServicioContactos();

    private Usuario usuarioActual = null; // Almacena el usuario logeado
    private String passwordActual = null; // Almacena la contraseña (necesaria para cifrado/descifrado)

    public void iniciar() {
        int opcion;
        do {
            mostrarMenuPrincipal();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        registrarUsuario();
                        break;
                    case 2:
                        login();
                        break;
                    case 0:
                        System.out.println("Saliendo");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n--- Gestor de Contactos ---");
        System.out.println("1. Registrar Usuario");
        System.out.println("2. Iniciar Sesión (Login)");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    // --- Lógica de Usuario ---

    private void registrarUsuario() {
        System.out.println("\n--- REGISTRO ---");
        // Leemos cada dato obligatoriamente y evitamos valores vacíos
        String nombreCompleto = leerNoVacio("Nombre completo: ");
        String usuario = leerNoVacio("Usuario: ");
        String password = leerNoVacio("Contraseña: ");
        String email = leerNoVacio("Correo electrónico: ");
        
        try {
            servicioUsuarios.registrarUsuario(nombreCompleto, usuario, password, email);
            System.out.println("Usuario registrado con éxito: " + usuario);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("\n--- LOGIN ---");
        String usuario = leerNoVacio("Usuario: ");
        String password = leerNoVacio("Contraseña: ");

        try {
            this.usuarioActual = servicioUsuarios.autenticar(usuario, password);
            this.passwordActual = password; 
            System.out.println(" Bienvenido, " + usuarioActual.getNombreCompleto() + "!");
            menuUsuario(); // Acceso al menú de contactos
        } catch (Exception e) {
            System.out.println("ERROR de autenticación: " + e.getMessage());
        }
    }

    // --- Menú de Contactos ---

    private void menuUsuario() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE CONTACTOS (" + usuarioActual.getNombreUsuario() + ") ---");
            System.out.println("1. Registrar Contacto");
            System.out.println("2. Ver Listado de Contactos");
            System.out.println("3. Compartir Contactos (Enviar Solicitud)");
            System.out.println("4. Revisar Solicitudes Recibidas");
            System.out.println("5. Ver Estado de Solicitudes Enviadas"); // Nueva opción
            System.out.println("0. Cerrar Sesión (Logout)");
            System.out.print("Seleccione una opción: ");
            
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: registrarContacto(); break;
                    case 2: verListadoContactos(); break;
                    case 3: solicitarCompartir(); break;
                    case 4: revisarSolicitudes(); break;
                    case 5: verEstadoSolicitudesEnviadas(); break; 
                    case 0:
                        System.out.println("🚪 Sesión cerrada.");
                        this.usuarioActual = null;
                        this.passwordActual = null;
                        return; // Sale del loop y vuelve al menú principal
                    default: System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número.");
                opcion = -1;
            }
        } while (true);
    }
    
    private void registrarContacto() {
        System.out.println("\n--- REGISTRAR CONTACTO ---");
        // Nombre y teléfono son obligatorios; email y url son opcionales
        String nombre = leerNoVacio("Nombre del contacto: ");
        String telefono = leerNoVacio("Teléfono: ");
        System.out.print("Correo electrónico (opcional): ");
        String email = scanner.nextLine().trim();
        System.out.print("URL de página personal (opcional, deje vacío para omitir): ");
        String url = scanner.nextLine().trim();

        try {
            Contacto nuevoContacto = new Contacto(nombre, telefono, email, url.isEmpty() ? null : url);
            servicioContactos.agregarContacto(usuarioActual, passwordActual, nuevoContacto);
            System.out.println("Contacto '" + nombre + "' agregado exitosamente.");
        } catch (Exception e) {
            System.out.println("ERROR al registrar contacto: " + e.getMessage());
        }
    }

    private void verListadoContactos() {
        try {
            List<Contacto> contactos = servicioContactos.obtenerContactosOrdenados(usuarioActual, passwordActual);
            
            if (contactos.isEmpty()) {
                System.out.println("Su lista de contactos está vacía.");
                return;
            }

            System.out.println("\n--- LISTADO DE CONTACTOS (" + contactos.size() + ") ---");
            for (int i = 0; i < contactos.size(); i++) {
                Contacto c = contactos.get(i);
                System.out.printf("[%d] %s\n", i + 1, c.getNombre());
            }

            System.out.print("\nIngrese el número del contacto para ver detalles (o 0 para volver): ");
            int indice = Integer.parseInt(scanner.nextLine());
            
            if (indice > 0 && indice <= contactos.size()) {
                Contacto c = contactos.get(indice - 1);
                System.out.println("\n--- DETALLE DE CONTACTO ---");
                System.out.println(c.toString());
            }

        } catch (Exception e) {
            System.out.println("ERROR al cargar contactos: " + e.getMessage());
        }
    }
    
    // --- Lógica de Compartir ---

    private void solicitarCompartir() {
        System.out.println("\n--- SOLICITAR COMPARTIR CONTACTOS ---");
        String nombreReceptor = leerNoVacio("Ingrese el nombre de usuario de la persona a la que desea compartir su lista: ");
        
        if (nombreReceptor.equals(usuarioActual.getNombreUsuario())) {
             System.out.println("No puede compartir la lista consigo mismo.");
             return;
        }

        try {
            servicioContactos.solicitarCompartir(usuarioActual, passwordActual, nombreReceptor);
            System.out.println("Solicitud enviada a " + nombreReceptor + " correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al enviar solicitud: " + e.getMessage());
        }
    }
    
    private void revisarSolicitudes() {
        try {
            List<SolicitudCompartir> solicitudes = servicioContactos.verSolicitudesPendientes(usuarioActual);
            
            if (solicitudes.isEmpty()) {
                System.out.println("No tiene solicitudes de contactos pendientes.");
                return;
            }

            System.out.println("\n--- SOLICITUDES PENDIENTES ---");
            for (int i = 0; i < solicitudes.size(); i++) {
                SolicitudCompartir s = solicitudes.get(i);
                System.out.printf("[%d] ID %s: Solicitud de %s\n", i + 1, s.getIdSolicitud(), s.getNombreSolicitante());
            }

            System.out.print("\nIngrese el número de la solicitud que desea aceptar (o 0 para volver): ");
            int indice = Integer.parseInt(scanner.nextLine());

            if (indice > 0 && indice <= solicitudes.size()) {
                SolicitudCompartir s = solicitudes.get(indice - 1);
                
                // Opción para aceptar o rechazar (simplificaremos a solo aceptar por ahora)
                System.out.print("¿Desea aceptar la lista de " + s.getNombreSolicitante() + "? (S/N): ");
                String respuesta = scanner.nextLine().trim().toUpperCase();

                if (respuesta.equals("S")) {
                    servicioContactos.aceptarSolicitud(s.getIdSolicitud(), usuarioActual, passwordActual);
                    System.out.println("Lista de contactos de " + s.getNombreSolicitante() + " importada con éxito!");
                } else {
                    // Lógica para rechazar (requeriría un método 'rechazarSolicitud' en ServicioContactos)
                    System.out.println("Solicitud de " + s.getNombreSolicitante() + " rechazada.");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR al procesar solicitud: " + e.getMessage());
        }
    }

    private void verEstadoSolicitudesEnviadas() {
    try {
        List<SolicitudCompartir> solicitudes = servicioContactos.verSolicitudesEnviadas(usuarioActual);

        if (solicitudes.isEmpty()) {
            System.out.println("No ha enviado solicitudes de contactos.");
            return;
        }

        System.out.println("\n--- SOLICITUDES ENVIADAS ---");
        for (int i = 0; i < solicitudes.size(); i++) {
            SolicitudCompartir s = solicitudes.get(i);
            System.out.printf("[%d] ID %s: A %s - Estado: %s\n", 
                              i + 1, 
                              s.getIdSolicitud(), 
                              s.getNombreDestinatario(), 
                              s.getEstado().getDescripcion());
        }

        // Opcionalmente, permitir ver el detalle de la solicitud
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();

    } catch (Exception e) {
        System.out.println("ERROR al cargar solicitudes enviadas: " + e.getMessage());
    }
}

    /**
     * Lee una entrada desde consola y obliga a que no sea vacía.
     * Reintenta hasta que el usuario ingrese un valor no vacío.
     */
    private String leerNoVacio(String prompt) {
        String valor;
        do {
            System.out.print(prompt);
            valor = scanner.nextLine();
            if (valor == null || valor.trim().isEmpty()) {
                System.out.println("El valor no puede estar vacío. Intente nuevamente.");
            }
        } while (valor == null || valor.trim().isEmpty());
        return valor.trim();
    }

}
