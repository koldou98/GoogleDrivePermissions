package controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.User;
import gui.JMain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.logging.Level;

/**
 * Clase encargada de gestionar el servicio
 */
public class Service {
    private static final String APPLICATION_NAME = "Proyecto Fin de Grado";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens"; //Se puede cambiar para elegir donde se van a
    // guardar los tokens

    /**
     * Global instance of the scopes required by this quickstart. If modifying these scopes, delete your previously
     * saved tokens/ folder.
     */
    // private static final List<String> SCOPES = DriveScopes.DRIVE_METADATA_READONLY);
    private static final java.util.Collection<String> SCOPES = DriveScopes.all(); // Hace que tengas acceso a todos
    // los datos de Drive
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = Service.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        in.close();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Método que crea el servicio de GoogleDrive. Si no se consigue la ejecución del programa se detiene
     *
     * @return Servicio obtenido
     */
    public static Drive getService() {
        // Build a new authorized API client service.
        NetHttpTransport HTTP_TRANSPORT;
        Drive service = null;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME).build();
            JMain.log.log(Level.INFO, "Servicio obtenido");
        } catch (GeneralSecurityException | IOException e) {
            JMain.log.log(Level.SEVERE, "Error al obtener servicio", e.getMessage());
        }
        return service;
    }


    /**
     * @param service Servicio de GoogleDrive. Si no lo consigue, la ejecución del programa se detiene
     * @return Devuelve el usuario al que pertenece el servicio
     */
    public static User getUser(Drive service) {
        User user = null;
        try {
            user = service.about().get().setFields("user").execute().getUser();
        } catch (IOException e) {
            JMain.log.log(Level.SEVERE, "No se pudo conseguir el usuario \n", e.getMessage());
        }
        return user;
    }

    public static String getRootId(Drive service) {
        String rootId = null;
        try {
            rootId = service.files().get("root").setFields("id").execute().getId();
        } catch (IOException e) {
            JMain.log.log(Level.SEVERE, "Error intentando obtener el id de la carpeta root \n", e.getMessage());
        }
        return rootId;
    }
}
