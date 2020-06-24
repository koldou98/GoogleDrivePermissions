package threads;

import gui.JMain;
import gui.JMainPane;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;

public class ConnectionThread implements Runnable {

    public static boolean status;
    public static boolean alive = true;
    private boolean connection;

    public ConnectionThread() {
    }

    @SuppressWarnings({"BusyWait"})
    @Override
    public void run() {
        while (alive) {
            try {
                InetAddress address = InetAddress.getByName("drive.google.com");

                if (!connection) {
                    JMain.log.log(Level.FINE, "Connection OK");
                    status = true;
                    JMainPane.lblCon.setIcon(new ImageIcon(getClass().getResource("/img/FileType/connectionOk.png")));
                }
                connection = address.isReachable(10);
            } catch (IOException e) {
                JMain.log.log(Level.FINE, "Connection Lost");
                JMainPane.lblCon.setIcon(new ImageIcon(getClass().getResource("/img/FileType/connectionError.png")));
                status = false;
            } finally {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    JMain.log.log(Level.SEVERE, e.getMessage());
                }
            }
        }
    }
}
