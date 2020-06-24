package gui;

import bbdd.Database;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.User;
import controller.Drives;
import controller.Files;
import controller.Service;
import gui.components.JSearchTextField;
import gui.components.ResultList;
import gui.renderers.JListCellRenderer;
import gui.renderers.JTreeCellRenderer;
import model.DriveFile;
import model.DriveUnit;
import model.Permissions;
import threads.ChangesThread;
import threads.ConnectionThread;
import threads.TreeThread;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

/**
 * Panel principal de la aplicación
 */
public class JMainPane extends JPanel {

    public static final Drive SERVICE = Service.getService();
    public static final User USER = Service.getUser(SERVICE);
    public static final String ROOTID = Database.getRootId();
    public static final JList<Object> list = new JList<>();
    public static final DefaultListModel<Object> listModel = new DefaultListModel<>();
    public static final DefaultMutableTreeNode root = new DefaultMutableTreeNode("GoogleDrive");
    private static final long serialVersionUID = -3374113519567475201L;
    public static JProgressBar progressBar = new JProgressBar();
    public static JLabel lblStatusMsg = new JLabel("");
    public static JSearchTextField textSearch = new JSearchTextField();
    public static JLabel lblCambios = new JLabel("");
    public static JTree tree = new JTree();
    public static JButton btnFilters = new JButton("Compartido con...");
    public static JLabel lblCon = new JLabel("");
    private final JLabel lblElements = new JLabel("");
    private List<DriveFile> myDriveFiles = Database.getMyDriveFiles();
    private List<DriveFile> sharedFiles = Database.getSharedFiles();
    private List<DriveUnit> teamDrives = Database.getTeamDriveList();
    private boolean search;
    private String selectedId;
    private String selectedName;

    @SuppressWarnings("rawtypes")

    public JMainPane() throws IOException {
        setLayout(new BorderLayout(5, 3));
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.EAST);
        scrollPane.setVisible(false);

        JPanel propsPane = new JPanel();
        propsPane.setMinimumSize(new Dimension(150, 10));
        scrollPane.setViewportView(propsPane);
        propsPane.setLayout(new BorderLayout(0, 0));

        JButton btnPermissions = new JButton("Editar Permisos");
        btnPermissions.addActionListener(e -> {
            JPermissions jPermissions = new JPermissions(selectedId, selectedName);
            jPermissions.setPermisos(selectedId);
            jPermissions.setVisible(true);
        });
        propsPane.add(btnPermissions, BorderLayout.NORTH);

        JPanel panelFields = new JPanel();
        propsPane.add(panelFields, BorderLayout.CENTER);
        panelFields.setLayout(new GridLayout(0, 1, 0, 0));

        JLabel lblNombre = new JLabel();
        panelFields.add(lblNombre);

        JLabel lblLink = new JLabel();
        panelFields.add(lblLink);

        JLabel lblPropietario = new JLabel();
        panelFields.add(lblPropietario);

        JLabel lblTamanno = new JLabel();
        panelFields.add(lblTamanno);

        JTextArea txtAreaCompartido = new JTextArea();
        txtAreaCompartido.setEditable(false);

        panelFields.add(txtAreaCompartido);

        Component horizontalStrut_1 = Box.createHorizontalStrut(20);
        horizontalStrut_1.setPreferredSize(new Dimension(200, 0));
        panelFields.add(horizontalStrut_1);

        tree = new JTree(root);
        tree.setEnabled(false);

        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new JTreeCellRenderer());
        checkTreeNodes();

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                if (!search) {
                    listModel.clear();
                    TreePath selectedPath = tree.getSelectionPath();
                    if (selectedPath != null) {
                        DefaultMutableTreeNode selectedNode =
                                (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                        lblElements.setText(selectedNode.getChildCount() + " elemento(s)");
                        Enumeration children = selectedNode.children();
                        while (children.hasMoreElements()) {
                            listModel.addElement(((DefaultMutableTreeNode) children.nextElement()).getUserObject());
                        }
                        list.setSelectedValue(selectedNode.getUserObject(), true);
                    }
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                if (!search) {
                    listModel.clear();
                    TreePath selectedPath = tree.getSelectionPath();
                    if (selectedPath != null) {
                        DefaultMutableTreeNode selectedNode =
                                (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                        lblElements.setText(selectedNode.getParent().getChildCount() + " elemento(s)");
                        Enumeration children = selectedNode.getParent().children();
                        while (children.hasMoreElements()) {
                            listModel.addElement(((DefaultMutableTreeNode) children.nextElement()).getUserObject());
                        }
                        list.setSelectedValue(selectedNode.getUserObject(), true);
                    }
                }
            }
        });
        tree.addTreeSelectionListener(e -> {
            TreePath selectedPath = tree.getSelectionPath();
            tree.scrollPathToVisible(selectedPath);
            DefaultMutableTreeNode selectedNode = new DefaultMutableTreeNode();
            if (selectedPath == null && !search) {
                listModel.clear();
                Enumeration children = root.children();
                while (children.hasMoreElements()) {
                    listModel.addElement(((DefaultMutableTreeNode) children.nextElement()).getUserObject());
                }
                list.setSelectedIndex(-1);
            }else if(selectedPath == null){
                Enumeration children = root.children();
                while (children.hasMoreElements()) {
                    listModel.addElement(((DefaultMutableTreeNode) children.nextElement()).getUserObject());
                }
            } else if (!tree.isExpanded(selectedPath) && !search) {
                selectedNode =
                        (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                listModel.clear();
                lblElements.setText(selectedNode.getParent().getChildCount() + " elemento(s)");
                Enumeration children = selectedNode.getParent().children();
                while (children.hasMoreElements()) {
                    listModel.addElement(((DefaultMutableTreeNode) children.nextElement()).getUserObject());
                }
                list.setSelectedValue(selectedNode.getUserObject(), true);
            } else if (tree.isExpanded(selectedPath) && !search) {
                selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                listModel.clear();
                lblElements.setText(selectedNode.getChildCount() + " elemento(s)");
                Enumeration children = selectedNode.children();
                while (children.hasMoreElements()) {
                    listModel.addElement(((DefaultMutableTreeNode) children.nextElement()).getUserObject());
                }
            } else {
                selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            }

            if (selectedNode.getUserObject() instanceof DriveFile) {
                System.out.println("Entra");
                DriveFile df = (DriveFile) selectedNode.getUserObject();
                String url = df.getWebViewLink();
                List<Permissions> filePermissions = Database.getFilePermissions(df.getFile_id());
                btnPermissions.setEnabled(df.getFile_id() != null && df.getDriveUnit() == null &&
                        ConnectionThread.status && (df.getOwnedByMe() || filePermissions.size() >= 1));
                selectedId = df.getFile_id();
                selectedName = df.getFile_name();
                if (df.getFile_name().length() > 18) {
                    StringBuilder nombre = new StringBuilder();
                    for (int i = 0; i < 18; i++) {
                        nombre.append(df.getFile_name().charAt(i));
                    }
                    nombre.append("...");
                    lblNombre.setToolTipText(df.getFile_name());
                    lblNombre.setText(" Nombre: " + nombre);
                } else {
                    lblNombre.setText(" Nombre: " + df.getFile_name());
                }
                if (df.getWebViewLink() != null) {
                    lblLink.setText("");
                    lblLink.setForeground(Color.blue.darker());
                    lblLink.setText(" Ver archivo en la web");
                    lblLink.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            try {
                                if (url.equals(df.getWebViewLink())) {
                                    Desktop.getDesktop().browse(new URI(df.getWebViewLink()));
                                }
                            } catch (IOException | URISyntaxException exception) {
                                exception.printStackTrace();
                            }
                        }
                    });
                } else {
                    lblLink.setText("");
                }
                if (df.getSize() != null) {
                    long size = df.getSize() / 1000;
                    lblTamanno.setText(" Tamaño: " + size);
                } else {
                    lblTamanno.setText("");
                }
                String owner = Database.getOwner(df.getFile_id(), df.getDriveUnit());
                if (!owner.equals("")) {
                    lblPropietario.setText(" Propietario: " + owner);
                } else {
                    lblPropietario.setText("");
                }
                List<String> shared = Database.getSharedUsers(df.getFile_id(), df.getDriveUnit());
                if (shared != null) {
                    if (shared.size() == 1) {
                        txtAreaCompartido.setText("Compartido con: " + shared.get(0));
                    } else {
                        StringBuilder sharedPeople = new StringBuilder();
                        for (int i = 0; i < shared.size(); i++) {
                            if (i == 0) {
                                sharedPeople = new StringBuilder(shared.get(i));
                            } else {
                                sharedPeople.append(System.getProperty("line.separator")).append(shared.get(i));
                            }
                            txtAreaCompartido.setText("Compartido con: \n" + sharedPeople);
                        }
                    }
                } else {
                    txtAreaCompartido.setText("");
                }
                scrollPane.setVisible(true);
            } else if (selectedNode.getUserObject() instanceof DriveUnit) {
                DriveUnit du = (DriveUnit) selectedNode.getUserObject();
                btnPermissions.setEnabled(false);
                lblNombre.setText(du.getName());
                scrollPane.setVisible(true);
            }
            if (list.getSelectedIndex() == -1) {
                scrollPane.setVisible(false);
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultMutableTreeNode selectedNode = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
                if (selectedNode != null && !selectedNode.isLeaf() && tree.isCollapsed(tree.getSelectionPath())) {
                    tree.expandPath(tree.getSelectionPath());
                } else if (selectedNode != null && !selectedNode.isLeaf() && tree.isExpanded(tree.getSelectionPath())) {
                    tree.collapsePath(tree.getSelectionPath());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (tree.getRowForLocation(e.getX(), e.getY()) == -1) {
                    tree.clearSelection();
                }
            }
        });
        // ScrollPaneTree
        JScrollPane scrollPaneTree = new JScrollPane();
        scrollPaneTree.setViewportView(tree);
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) && list.isEnabled()) {

                    String id;
                    if (list.getSelectedValue() instanceof DriveFile) {
                        DriveFile df = (DriveFile) list.getSelectedValue();
                        if (df.getFile_id() != null) {
                            id = df.getFile_id();
                        } else {
                            id = df.getFile_name();
                        }
                    } else {
                        DriveUnit du = (DriveUnit) list.getSelectedValue();
                        id = du.getUnit_id();
                    }
                    TreePath path = new TreePath(TreeThread.nodeHashMap.get(id));
                    tree.setSelectionPath(path);
                    tree.expandPath(path);
                    tree.scrollPathToVisible(path);

                } else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && list.isEnabled()) {
                    String id;
                    if (list.getSelectedValue() instanceof DriveFile) {
                        DriveFile df = (DriveFile) list.getSelectedValue();
                        if (df.getFile_id() != null) {
                            id = df.getFile_id();
                        } else {
                            id = df.getFile_name();
                        }
                    } else {
                        DriveUnit du = (DriveUnit) list.getSelectedValue();
                        id = du.getUnit_id();
                    }
                    TreePath path = new TreePath(TreeThread.nodeHashMap.get(id));
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
            }
        });
        list.setEnabled(false);
        list.setModel(listModel);
        list.setCellRenderer(new JListCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (list.isEnabled()) {
                    String id;
                    if (list.getSelectedValue() instanceof DriveFile) {
                        DriveFile df = (DriveFile) list.getSelectedValue();
                        if (df.getFile_id() != null) {
                            id = df.getFile_id();
                        } else {
                            id = df.getFile_name();
                        }
                    } else {
                        DriveUnit du = (DriveUnit) list.getSelectedValue();
                        id = du.getUnit_id();
                    }
                    TreePath path = new TreePath(TreeThread.nodeHashMap.get(id));
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
            }

            //* Cuando se hace click en un archivo con múltiples padres, se redirige al fichero de la raíz, debido a que
            //* comparten los mismos permisos.
            @Override
            public void mouseClicked(MouseEvent e) {
                if (list.isEnabled() && e.getClickCount() == 2 && !e.isConsumed()) {
                    String id;
                    if (list.getSelectedValue() instanceof DriveFile) {
                        DriveFile df = (DriveFile) list.getSelectedValue();
                        if (df.getFile_id() != null) {
                            id = df.getFile_id();
                        } else {
                            id = df.getFile_name();
                        }
                    } else {
                        DriveUnit du = (DriveUnit) list.getSelectedValue();
                        id = du.getUnit_id();
                    }
                    TreePath path = new TreePath(TreeThread.nodeHashMap.get(id));
                    tree.setSelectionPath(path);
                    tree.expandPath(path);
                    tree.scrollPathToVisible(path);
                }
            }
        });
        // ScrollPaneList
        JScrollPane scrollPaneList = new JScrollPane();
        scrollPaneList.setViewportView(list);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneTree, scrollPaneList);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
//        splitPane.setResizeWeight(0.05);
//        splitPane.setDividerLocation(0.4);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        JPanel dataPane = new JPanel();
        add(dataPane, BorderLayout.SOUTH);
        GridBagLayout gbl_dataPane = new GridBagLayout();
        gbl_dataPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_dataPane.rowHeights = new int[]{0, 0};
        gbl_dataPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_dataPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        dataPane.setLayout(gbl_dataPane);


        GridBagConstraints gbc_lblElements = new GridBagConstraints();
        gbc_lblElements.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblElements.insets = new Insets(0, 0, 0, 5);
        gbc_lblElements.gridx = 0;
        gbc_lblElements.gridy = 0;
        dataPane.add(lblElements, gbc_lblElements);

        Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
        GridBagConstraints gbc_rigidArea = new GridBagConstraints();
        gbc_rigidArea.gridwidth = 3;
        gbc_rigidArea.insets = new Insets(0, 0, 0, 5);
        gbc_rigidArea.gridx = 1;
        gbc_rigidArea.gridy = 0;
        dataPane.add(rigidArea, gbc_rigidArea);


        GridBagConstraints gbc_lblStatusMsg = new GridBagConstraints();
        gbc_lblStatusMsg.insets = new Insets(0, 0, 0, 5);
        gbc_lblStatusMsg.gridx = 4;
        gbc_lblStatusMsg.gridy = 0;
        dataPane.add(lblStatusMsg, gbc_lblStatusMsg);


        GridBagConstraints gbc_progressBar = new GridBagConstraints();
        gbc_progressBar.insets = new Insets(0, 0, 0, 5);
        gbc_progressBar.gridx = 5;
        gbc_progressBar.gridy = 0;
        dataPane.add(progressBar, gbc_progressBar);

        Component horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 0, 5);
        gbc_horizontalStrut.gridx = 6;
        gbc_horizontalStrut.gridy = 0;
        dataPane.add(horizontalStrut, gbc_horizontalStrut);

        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel.gridx = 7;
        gbc_lblNewLabel.gridy = 0;
        dataPane.add(lblCambios, gbc_lblNewLabel);


        GridBagConstraints gbc_lblCon = new GridBagConstraints();
        gbc_lblCon.anchor = GridBagConstraints.EAST;
        gbc_lblCon.gridx = 8;
        gbc_lblCon.gridy = 0;
        dataPane.add(lblCon, gbc_lblCon);

        JPanel searchPane = new JPanel();
        add(searchPane, BorderLayout.NORTH);
        GridBagLayout gbl_searchPane = new GridBagLayout();
        gbl_searchPane.columnWidths = new int[]{0, 148, 26, 203, 127, 0};
        gbl_searchPane.rowHeights = new int[]{23, 0};
        gbl_searchPane.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        gbl_searchPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        searchPane.setLayout(gbl_searchPane);

        textSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ResultList rl;
                DefaultListModel<Object> mod = new DefaultListModel<>();
                Database.getSearchData(textSearch.getText()).forEach(mod::addElement);
                if (!textSearch.getText().trim().equals("")) {
                    search = true;
                    rl = new ResultList(textSearch.getWidth(), mod, new JListCellRenderer());
                    rl.list();
                    rl.show(textSearch, 0, textSearch.getHeight());
                }

            }
        });
        textSearch.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textSearch.getText().trim().equals("Buscar nombre de archivo, nombre de usuario o correo del " +
                        "usuario")) {
                    textSearch.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textSearch.getText().trim().equals("")) {
                    search = false;
                    textSearch.setText("Buscar nombre de archivo, nombre de usuario o correo del usuario");
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
                    Enumeration children = node.children();
                    while (children.hasMoreElements()) {
                        node = (DefaultMutableTreeNode) children.nextElement();
                        tree.collapsePath(new TreePath(node.getPath()));
                    }
                }
            }
        });
        textSearch.setIcon(new ImageIcon(getClass().getResource("/img/FileType/search.png")));
        GridBagConstraints gbc_searchPanel = new GridBagConstraints();
        gbc_searchPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_searchPanel.gridwidth = 3;
        gbc_searchPanel.insets = new Insets(0, 0, 0, 5);
        gbc_searchPanel.gridx = 1;
        gbc_searchPanel.gridy = 0;
        searchPane.add(textSearch, gbc_searchPanel);


        btnFilters.setEnabled(false);
        btnFilters.addActionListener(e -> {
            search = true;
            List<DriveFile> list;
            if (btnFilters.getText().equals("Compartido con...")) {
                btnFilters.setText("Con el dominio");
                listModel.clear();
                list = Database.getDomainSharedFiles();
                if (list.size() > 0) {
                    list.forEach(listModel::addElement);
                }
            } else if (btnFilters.getText().equals("Con el dominio")) {
                btnFilters.setText("Con cualquiera");
                listModel.clear();
                list = Database.getAnyoneSharedFiles();
                if (list.size() > 0) {
                    list.forEach(listModel::addElement);
                }
            } else {
                search = false;
                btnFilters.setText("Compartido con...");
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
                Enumeration children = node.children();
                while (children.hasMoreElements()) {
                    node = (DefaultMutableTreeNode) children.nextElement();
                    tree.collapsePath(new TreePath(node.getPath()));
                }
            }
        });
        GridBagConstraints gbc_btnFilters = new GridBagConstraints();
        gbc_btnFilters.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnFilters.gridx = 4;
        gbc_btnFilters.gridy = 0;
        searchPane.add(btnFilters, gbc_btnFilters);
    }

    /**
     * Comprueba si las unidades de equipo almacenadas en la BBDD son las mismas que se encuentran en drive
     *
     * @param teamDrives Lista de las unidades de equipo almacenadas en la BBDD
     * @return Lista de las unidades de equipo de tipo <code>{{@link DriveUnit}}</code>
     * @throws IOException Posible excepción que pueda ocurrir en la ejecución
     */
    public static List<DriveUnit> checkTeamDrives(List<DriveUnit> teamDrives) throws IOException {
        if (!Drives.equalTeamDrives(teamDrives)) {
            Database.addDriveUnits();
            return Database.getTeamDriveList();
        } else {
            return teamDrives;
        }
    }

    /**
     * Comprueba que nodos se tienen que visualizar. Para ello obtiene los archivos de la BBDD y los actualiza
     * respecto a la ejecución anterior.
     *
     * @throws IOException Excepción que puede ocurrir al intentar obtener los datos
     */
    private void checkTreeNodes() throws IOException {
        JMain.jLoad.setVisible(true);
        //* Si no se encuentra ningún archivo en la BBDD, se cargan de Drive
        if (myDriveFiles.size() < 2 && sharedFiles.size() < 1) {
            JMain.changeToken = "";
            Files.getMyDriveFiles();
            Files.getSharedFiles();
            Database.deleteDriveUnits();
            Database.addDriveUnits();
            myDriveFiles = Database.getMyDriveFiles();
            sharedFiles = Database.getSharedFiles();
            teamDrives = Database.getTeamDriveList();
            teamDrives.forEach(driveUnit -> {
                try {
                    Files.getTeamDriveFiles(driveUnit.getUnit_id());
                } catch (IOException e) {
                    JMain.log.log(Level.SEVERE, e.getMessage());
                }
            });
        }
        if (ConnectionThread.status) {
            teamDrives = checkTeamDrives(teamDrives);
            getFileChanges();
        }
        if (myDriveFiles.size() == 1 && teamDrives.size() == 0 && sharedFiles.size() == 0) {
            JOptionPane.showMessageDialog(null, "No se han encontrado archivos en GoogleDrive");
        }
        JMain.treeThread = new Thread(new TreeThread());
        JMain.treeThread.start();

        if (myDriveFiles.size() == 1 && teamDrives.size() == 0 && sharedFiles.size() == 0) {
            JOptionPane.showMessageDialog(null, "No se han encontrado archivos en GoogleDrive");
        }
        JMain.changesThread = new Thread(new ChangesThread(teamDrives), "changesThread");
        JMain.changesThread.start();
        JMain.jLoad.setVisible(false);
    }

    /**
     * Carga en la BBDD todos los cambios realizados sobre los ficheros desde la ejecución anterior
     */
    private void getFileChanges() throws IOException {
        if (!JMain.changeToken.equals("")) {
            List<Change> changeList =
                    SERVICE.changes().list(JMain.changeToken).setIncludeRemoved(true).execute().getChanges();
            changeList.forEach(change -> {
                try {
                    Files.getFile(change.getFileId());
                } catch (IOException e) {
                    JMain.log.log(Level.SEVERE, e.getMessage());
                }
            });
        }
    }
}
