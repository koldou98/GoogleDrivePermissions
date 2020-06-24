package controller;

import bbdd.Database;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import gui.JMain;
import gui.JMainPane;
import threads.LoadDBThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Clase que gestiona la obtención de los ficheros.
 */
public class Files {

    /**
     * Método que obtiene todos los ficheros que tienen al usuario como propietario.
     *
     * @throws IOException Excepción que puede ocurrir al realizar la consulta
     */
    public static void getMyDriveFiles() throws IOException {
        List<File> list = new ArrayList<>();
        Drive.Files.List fl = JMainPane.SERVICE.files().list();
        fl.setFields("files(id, name, mimeType, parents, ownedByMe, shared, size, permissions, webViewLink, " +
                "teamDriveId, owners), nextPageToken");
        fl.setQ("trashed=false and '" + JMainPane.USER.getPermissionId() + "' in owners");
        executeQuery(list, fl);
    }

    /**
     * Método que obtiene todos los ficheros compartidos con el usuario
     *
     * @throws IOException Excepción que puede ocurrir al realizar la consulta
     */
    public static void getSharedFiles() throws IOException {
        List<File> list = new ArrayList<>();
        Drive.Files.List fl = JMainPane.SERVICE.files().list();
        fl.setFields("files(id, name, mimeType, parents, ownedByMe, shared, size, permissions, webViewLink, " +
                "teamDriveId, owners), nextPageToken");
        fl.setQ("trashed=false and not '" + JMainPane.USER.getPermissionId() + "' in owners");
        executeQuery(list, fl);
    }

    /**
     * Método que obtiene los archivos de una unidad de equipo.
     *
     * @param teamDriveId Id de la unidad de equipo donde se van a buscar los datos
     * @throws IOException Excepción que puede ocurrir al realizar la consulta
     */
    public static void getTeamDriveFiles(String teamDriveId) throws IOException {
        List<File> list = new ArrayList<>();
        Drive.Files.List fl = JMainPane.SERVICE.files().list();
        fl.setFields("files(id, name, mimeType, parents, ownedByMe, shared, size, permissions, webViewLink, " +
                "teamDriveId, owners), nextPageToken");
        fl.setSupportsAllDrives(true);
        fl.setIncludeItemsFromAllDrives(true);
        fl.setCorpora("drive");
        fl.setDriveId(teamDriveId);
        fl.setQ("trashed = false");
        executeQuery(list, fl);
    }

    /**
     * Método que ejecuta la consulta y almacena los ficheros en una lista. Mientras se almacenan los ficheros en la
     * lista, otro hilo va añadiendo los archivos de la lista en la BBDD
     *
     * @param list Lista donde se almacenan los ficheros
     * @param fl   Consulta con los parámetros deseados seleccionados
     * @throws IOException Excepción que puede ocurrir al realizar la consulta
     */
    private static void executeQuery(List<File> list, Drive.Files.List fl) throws IOException {
        FileList result = fl.execute();
        list.addAll(result.getFiles());
        LoadDBThread.addFilesToList(list);
        while (result.getNextPageToken() != null && result.getNextPageToken().length() > 0) {
            list.clear();
            result = fl.setPageToken(result.getNextPageToken()).execute();
            list.addAll(result.getFiles());
            LoadDBThread.addFilesToList(list);
        }
        JMain.log.log(Level.FINE, "Archivos almacenados en la BBDD");
    }

    /**
     * Obtiene un fichero de Google Drive para su posterior gestión en la BBDD
     *
     * @param fileId Id del fichero que se quiere obtener de Google Drive
     * @throws IOException Excepción que puede ocurrir
     */
    public static void getFile(String fileId) throws IOException {
        Drive.Files.Get fl = JMainPane.SERVICE.files().get(fileId);
        fl.setFields("id, name, mimeType, parents, ownedByMe, shared, size, permissions, webViewLink, " +
                "teamDriveId, owners, trashed");
        fl.setSupportsAllDrives(true);
        File result = fl.execute();
        Database.manageModifiedFiles(result);
    }
}