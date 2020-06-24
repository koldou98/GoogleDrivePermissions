package controller;

import javax.swing.ImageIcon;

/**
 * Clase encargada de gestioar los iconos.
 */
public class Icons {

    /**
     * Obtiene el icono para cada tipo de ficheros.
     *
     * @param iconType Tipo del fichero
     * @return Icono del fichero
     */
    public static ImageIcon getIcon(String iconType) {
        switch (iconType) {
            case "MyDriveFolder":
                return new ImageIcon(Icons.class.getResource("/img/FileType/MyDrive.png"));
            case "SharedFilesFolder":
                return new ImageIcon(Icons.class.getResource("/img/FileType/SharedFiles.png"));
            case "SharedDrivesFolder":
                return new ImageIcon(Icons.class.getResource("/img/FileType/SharedDrives.png"));
            case "application/msword":
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return new ImageIcon(Icons.class.getResource("/img/FileType/Ms-Word.png"));
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return new ImageIcon(Icons.class.getResource("/img/FileType/Ms-Excel.png"));
            case "application/vnd.ms-powerpoint":
            case "application/vnd.ms-powerpoint.presentation.macroenabled.12":
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
            case "application/vnd.oasis.opendocument.presentation":
                return new ImageIcon(Icons.class.getResource("/img/FileType/Ms-ppt.png"));
            case "application/vnd.google-apps.document":
                return new ImageIcon(Icons.class.getResource("/img/FileType/GDoc.png"));
            case "application/vnd.google-apps.spreadsheet":
                return new ImageIcon(Icons.class.getResource("/img/FileType/GSheet.png"));
            case "application/vnd.google-apps.presentation":
                return new ImageIcon(Icons.class.getResource("/img/FileType/GSlides.png"));
            default:
                return new ImageIcon(Icons.class.getResource("/img/FileType/unknown.png"));
        }
    }
}
