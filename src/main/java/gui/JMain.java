package gui;

import bbdd.Database;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import gui.renderers.JListCellRenderer;
import org.h2.store.fs.FileUtils;
import threads.ChangesThread;
import threads.ConnectionThread;
import threads.LoadDBThread;
import threads.TreeThread;

import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Properties;
import java.util.logging.*;

/**
 * Clase principal que inicia la ejecución el programa.
 */
public class JMain extends JFrame {


    private static final long serialVersionUID = 4877438655413805116L;
    private static final Thread dbThread = new Thread(new LoadDBThread(), "bdThread");
    private static final Thread connectionThread = new Thread(new ConnectionThread(), "connectionThread");
    public static Thread changesThread;
    public static Logger log;
    public static Thread treeThread;
    public static String changeToken = "";
    public static JMenu mnRefresh;
    public static JMenu mnCerrarSesion;
    public static JMenu mnTheme;
    public static JCheckBoxMenuItem chckbxmntmDarkTheme = new JCheckBoxMenuItem("Tema oscuro");
    public static DefaultTreeCellRenderer defaultRenderer;
    public static JMenuBar menuBar;
    public static JLoad jLoad;
    private static String theme = "";
    private static Properties properties;
    private static CardLayout cardLayout;
    private  static boolean logout = false;

    /**
     * Create the frame.
     *
     * @param cardLayout CardLayout sobre el que se usaran los distintos paneles
     * @throws IOException Excepción que puede ocurrir en los distintos métodos
     */
    public JMain(CardLayout cardLayout) throws IOException {

        JMain.cardLayout = cardLayout;
        setTitle("Google Drive Permissions");
        setIconImage(Toolkit.getDefaultToolkit().getImage(JMain.class.getResource("/img/BlackDriveIcon.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    LoadDBThread.em.close();
                    LoadDBThread.alive = false;
                    ChangesThread.alive = false;
                    TreeThread.alive = false;
                    ConnectionThread.alive = false;
                    Database.emf.close();
                    log.log(Level.INFO, "Entity Manager Factory Closed");
                    guardarProperties();
                } catch (IOException exception) {
                    log.log(Level.SEVERE, exception.getMessage());
                }
            }
        });

        setBounds(100, 100, 750, 563);
        setLocationRelativeTo(null);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        mnRefresh = new JMenu("Refrescar");
        menuBar.add(mnRefresh);

        JMenuItem mntmRefrescar = new JMenuItem("Actualizar datos aplicación");
        mntmRefrescar.addActionListener(e -> {
                    treeThread = new Thread(new TreeThread());
                    treeThread.start();
                }
        );
        mnRefresh.add(mntmRefrescar);

        mnTheme = new JMenu("Tema");
        menuBar.add(mnTheme);
        boolean sel = chckbxmntmDarkTheme.isSelected();
        chckbxmntmDarkTheme = new JCheckBoxMenuItem("Tema oscuro");
        chckbxmntmDarkTheme.setSelected(sel);
        chckbxmntmDarkTheme.addItemListener(e -> {
            if (chckbxmntmDarkTheme.isSelected()) {
                FlatDarculaLaf.install();
            } else {
                FlatIntelliJLaf.install();
            }
            defaultRenderer = new DefaultTreeCellRenderer();
            SwingUtilities.updateComponentTreeUI(JMain.this);
            JMainPane.list.setCellRenderer(new JListCellRenderer());
            JMainPane.list.repaint();
        });
        mnTheme.add(chckbxmntmDarkTheme);

        mnCerrarSesion = new JMenu("Cerrar sesión");
        menuBar.add(mnCerrarSesion);

        JMenuItem mntmLogOut = new JMenuItem("Cerrar sesión");
        mnCerrarSesion.add(mntmLogOut);
        mntmLogOut.addActionListener(e -> {
            logout = true;
            ConnectionThread.alive = false;
            ChangesThread.alive = false;
            ChangesThread.previousChangeToken = "";
            LoadDBThread.alive = false;
            Database.emf.close();
            jLoad.dispose();
            dispose();

            File db = new File("db.mv.db");
            if (db.exists()) {
                db.deleteOnExit();
            }
            File dblog = new File("db.trace.db");
            if (dblog.exists()) {
                dblog.deleteOnExit();
            }
            File props = new File("./properties");
            if(props.exists()){
                FileUtils.deleteRecursive(props.getPath(), true);
            }
            for (int i = 0; i < log.getHandlers().length; i++) {
                log.removeHandler(log.getHandlers()[i]);
            }
            File token = new File("./tokens");
            if(token.exists()){
                FileUtils.deleteRecursive(token.getPath(), true);
            }

            for (int i = 0; i < log.getHandlers().length; i++) {
                log.removeHandler(log.getHandlers()[i]);
            }
            File logs = new File("./logs");
            if (logs.exists()) {
                FileUtils.deleteRecursive(logs.getPath(), true);
            }
        });
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        JMain.cardLayout = new CardLayout(0, 0);
        contentPane.setLayout(JMain.cardLayout);

        // Paneles para el layout
        JMainPane mainPane = new JMainPane();
        //Añadir los paneles a la ventana
        contentPane.add(mainPane);
    }

    private static void cargarProperties() throws IOException {
        properties = new Properties();
        File f = new File("./properties/properties.ini");
        if (f.exists()) {
            properties.loadFromXML(new FileInputStream("./properties/properties.ini"));
            changeToken = properties.getProperty("changeToken");
            theme = properties.getProperty("theme");
        }
        log.log(Level.INFO, "Properties cargadas");
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void guardarProperties() throws IOException {
        if (!logout) {
            FileOutputStream fos;
            properties = new Properties();
            File f = new File("./properties/properties.ini");
            if (!f.exists()) {
                new File("./properties").mkdir();
                f.createNewFile();
            }
            fos = new FileOutputStream("./properties/properties.ini");
            PrintStream ps = new PrintStream(fos);
            properties.setProperty("changeToken", ChangesThread.previousChangeToken);
            properties.setProperty("theme", UIManager.getLookAndFeel().toString());
            properties.storeToXML(fos, null);
            fos.close();
            ps.close();
            log.log(Level.INFO, "Properties guardadas");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        Database.emf = Persistence.createEntityManagerFactory("mainDB");
        try {
            File logDir = new File("./logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            log = Logger.getLogger("Logger");
            Handler c = new ConsoleHandler();
            c.setLevel(Level.ALL);
            log.addHandler(c);
            log.setLevel(Level.ALL);
            log.setUseParentHandlers(false);
            Handler h = new FileHandler("./logs/log.xml");
            h.setLevel(Level.INFO);
            log.addHandler(h);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        try {
            cargarProperties();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        connectionThread.start();
        dbThread.start();
        try {
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("ScrollBar.showButtons", true);
            if (theme == null || !theme.equals("[Flat Darcula Look and Feel - com.formdev.flatlaf" +
                    ".FlatDarculaLaf]")) {
                FlatIntelliJLaf.install();
            } else {
                chckbxmntmDarkTheme.setSelected(true);
                FlatDarculaLaf.install();
            }
            defaultRenderer = new DefaultTreeCellRenderer();
            log.log(Level.INFO, "Inicio el programa");
            jLoad = new JLoad();

            JMain frame = new JMain(cardLayout);
            frame.setVisible(true);
        } catch (Exception e) {
            Database.emf.close();
            log.log(Level.SEVERE, "Error", e);
        }
    }
}
