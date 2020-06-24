package threads;

import com.google.api.services.drive.model.Change;
import controller.Files;
import gui.JMain;
import gui.JMainPane;
import model.DriveUnit;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Hilo encargado de comprobar si se realizan modificaciones en los archivos de Drive. Si se realizan modificaciones
 * actualiza los archivos de la BBDD
 */
public class ChangesThread implements Runnable {
    public static boolean alive = true;
    public static String previousChangeToken;
    private List<DriveUnit> driveUnitList;

    public ChangesThread(List<DriveUnit> driveUnitList) {
        this.driveUnitList = driveUnitList;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        while (alive) {
            try {
                if (ConnectionThread.status) {

                    String changeToken = JMainPane.SERVICE.changes().getStartPageToken().execute().getStartPageToken();
                    if (previousChangeToken == null) {
                        previousChangeToken = changeToken;
                    }
                    if (!previousChangeToken.equals(changeToken)) {
                        driveUnitList = JMainPane.checkTeamDrives(driveUnitList);
                        List<Change> changes =
                                JMainPane.SERVICE.changes().list(previousChangeToken).setIncludeRemoved(true).execute().getChanges();

                        changes.forEach(change -> {
                            try {
                                Files.getFile(change.getFileId());
                            } catch (IOException e) {
                                JMain.log.log(Level.SEVERE, e.getMessage());
                            }
                        });
                        previousChangeToken = changeToken;
                        JMainPane.lblCambios.setText("Se han encontrado cambios");
                        JMainPane.lblCambios.setIcon(new ImageIcon(this.getClass().getResource("/img/FileType/changes.png")));
                        JMainPane.lblCambios.setVisible(true);
                        JMain.log.log(Level.FINE, "Changes");
                    }
                }
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                JMain.log.log(Level.SEVERE, e.getMessage());
            }

        }
    }
}
