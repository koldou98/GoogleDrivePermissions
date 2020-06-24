package threads;

import bbdd.Database;
import gui.JMain;
import gui.JMainPane;
import model.DriveFile;
import model.DriveUnit;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Hilo encargado de la generación del árbol
 */
public class TreeThread implements Runnable {
    public static boolean alive = true;
    public static HashMap<String, TreeNode[]> nodeHashMap = new HashMap<>();
    private final List<DriveFile> myDriveFiles = Database.getMyDriveFiles();
    private final List<DriveFile> sharedFiles = Database.getSharedFiles();
    private final List<DriveUnit> teamDrives = Database.getTeamDriveList();
    private final JTree tree = JMainPane.tree;
    private final DefaultMutableTreeNode root = JMainPane.root;
    private final DefaultListModel<Object> listModel = JMainPane.listModel;
    private final JProgressBar progressBar = JMainPane.progressBar;
    private final JLabel label = JMainPane.lblStatusMsg;
    private final long fileCount = Database.getFileCount() - 1;
    private int cont;

    public TreeThread(){

    }
//    public TreeThread(JTree tree, DefaultMutableTreeNode root, List<DriveFile> myDriveFiles,
//                      List<DriveFile> sharedFiles, List<DriveUnit> teamDrives, DefaultListModel<Object> listModel) {
//        this.tree = tree;
//        this.root = root;
//        this.myDriveFiles = myDriveFiles;
//        this.sharedFiles = sharedFiles;
//        this.teamDrives = teamDrives;
//        this.listModel = listModel;
//    }

    /**
     * Genera el árbol de forma recursiva.
     *
     * @param parentId   Id del padre
     * @param parentNode Nodo padre
     */
    private void generateTree(String parentId, DefaultMutableTreeNode parentNode) {
        Database.getChildrenFiles(parentId).forEach(driveFile -> {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(driveFile);
            parentNode.add(node);
            JMainPane.progressBar.setValue(++cont);
            generateTree(driveFile.getFile_id(), node);
        });
    }

    /**
     * Añade las unidades de equipo al árbol
     *
     * @param sharedDrives Nodo del árbol
     * @param teamDrives   Lista con los nombres de las unidades de equipo
     */
    private void addSharedDrivesChild(DefaultMutableTreeNode sharedDrives,
                                      List<DriveUnit> teamDrives) {
        teamDrives.forEach(driveUnit -> {
            DefaultMutableTreeNode sharedDriveNode = new DefaultMutableTreeNode(driveUnit);
            sharedDrives.add(sharedDriveNode);
//            model.reload(sharedDrives);
            generateTree(driveUnit.getUnit_id(), sharedDriveNode);
        });
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public void run() {
        progressBar.setVisible(true);
        JMainPane.textSearch.setEnabled(false);
        JMainPane.textSearch.setText("Buscar nombre de archivo, nombre de usuario o correo del usuario");
        JMainPane.btnFilters.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum((int) fileCount);
        label.setText("Generando árbol...");
        label.setVisible(true);
        JMain.mnCerrarSesion.setEnabled(false);
        JMain.mnRefresh.setEnabled(false);
        JMain.mnTheme.setEnabled(false);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) model.getRoot();
        parentNode.removeAllChildren();
        listModel.clear();
        if (myDriveFiles.size() > 1) {
            DefaultMutableTreeNode myDriveNode = new DefaultMutableTreeNode(new DriveFile("My Drive", "MyDriveFolder"));
            root.add(myDriveNode);
            listModel.addElement(myDriveNode.getUserObject());
            DefaultMutableTreeNode noDirectParentFiles = new DefaultMutableTreeNode(new DriveFile("Ficheros sin " +
                    "padre directo", "application/vnd.google-apps.folder"));
            model.reload(root);
            myDriveNode.add(noDirectParentFiles);
            generateTree(JMainPane.ROOTID, myDriveNode);
            model.reload(myDriveNode);
            generateTree("SharedParent", noDirectParentFiles);
        }
        if (sharedFiles.size() > 0) {
            DefaultMutableTreeNode sharedFilesNode = new DefaultMutableTreeNode(new DriveFile("Shared Files",
                    "SharedFilesFolder"));
            root.add(sharedFilesNode);
            if (listModel.getElementAt(0).toString().equals(new DriveFile("My Drive", "MyDriveFolder").toString())) {
                listModel.addElement(sharedFilesNode.getUserObject());
            }
            model.reload(root);
            generateTree("SharedFolderParent", sharedFilesNode);
        }
        if (teamDrives.size() > 0) {
            DefaultMutableTreeNode sharedDriveNode = new DefaultMutableTreeNode(new DriveFile("Shared Drives",
                    "SharedDrivesFolder"));
            root.add(sharedDriveNode);
            if (listModel.getElementAt(0).toString().equals(new DriveFile("My Drive", "MyDriveFolder").toString())) {
                listModel.addElement(sharedDriveNode.getUserObject());
            }
            model.reload(root);
            addSharedDrivesChild(sharedDriveNode, teamDrives);
        }

        model.reload();
        tree.setEnabled(true);
        JMain.log.log(Level.INFO, "Carga inicial árbol");
        label.setText("Indexando datos...");
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum((int) fileCount);
        cont = 1;
                
        Enumeration preOrderEnumeration = root.preorderEnumeration();
        while (preOrderEnumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) preOrderEnumeration.nextElement();
            if (node.getUserObject() instanceof DriveFile) {
                DriveFile df = (DriveFile) node.getUserObject();
                if (df.getFile_id() != null) {
                    nodeHashMap.put(df.getFile_id(), node.getPath());
                } else {
                    nodeHashMap.put(df.getFile_name(), node.getPath());
                }
                progressBar.setValue(++cont);
            } else if (node.getUserObject() instanceof DriveUnit) {
                DriveUnit du = (DriveUnit) node.getUserObject();
                nodeHashMap.put(du.getUnit_id(), node.getPath());
                progressBar.setValue(++cont);
            }
        }
        JMainPane.list.setEnabled(true);
        JMainPane.textSearch.setEnabled(true);
        JMainPane.lblCambios.setVisible(false);
        JMain.mnCerrarSesion.setEnabled(true);
        JMainPane.btnFilters.setEnabled(true);
        JMain.mnRefresh.setEnabled(true);
        JMain.mnTheme.setEnabled(true);
        JMain.menuBar.repaint();
        progressBar.setValue(0);
        progressBar.setVisible(false);
        label.setVisible(false);
    }
}
