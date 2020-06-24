package threads;

import bbdd.Database;
import com.google.api.services.drive.model.File;
import gui.JMain;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Hilo que se encargará de almacenar y modificar los ficheros en la BBDD.
 */
public class LoadDBThread implements Runnable {
    //* Vector con cast para el ArrayList que se recibe en el método.
    private static final Vector<File> fileList = new Vector<>(new ArrayList<>());
    public static EntityManager em;
    public static List<File> addedFiles = new ArrayList<>();
    public static boolean alive = true;

    public LoadDBThread() {
    }

    /**
     * Añade los ficheros obtenidos a la lista para que posteriormente se añadan a la BBDD
     *
     * @param list <code>{@link ArrayList}</code> que contiene la lista de ficheros que se van a añadir a la BBDD
     */
    public static void addFilesToList(List<File> list) {
        fileList.addAll(list);
    }

    public static void clearList() {
        fileList.clear();
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        em = Database.emf.createEntityManager();
        while (alive) {
            if (!fileList.isEmpty()) {
                em.getTransaction().begin();
                fileList.forEach(file -> Database.addFilesToDatabase(em, file));
                em.getTransaction().commit();
                addedFiles.addAll(fileList);
                clearList();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                JMain.log.log(Level.SEVERE, e.getMessage());
            }
        }
    }
}
