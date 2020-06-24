package gui;

import bbdd.Database;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.model.Permission;
import gui.components.UserPermissions;
import model.Permissions;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Ventana encargada de la gestión de los permisos
 */
public class JPermissions extends JFrame {

    private static final long serialVersionUID = 4424305389515392600L;
    private static final int SEPARATION = 20;
    private final JPanel panel_1;

    public JPermissions(String id, String name) {

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("Permisos: " + name);
        setSize(485, 285);
        setIconImage(Toolkit.getDefaultToolkit().getImage(JPermissions.class.getResource("/img/BlackDriveIcon.png")));
        setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        setContentPane(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        panel.add(tabbedPane);

        JPanel addUserPane = new JPanel();
        tabbedPane.addTab("Permisos usuario", null, addUserPane, null);
        addUserPane.setLayout(null);

        JLabel lblMail = new JLabel("Correo del usuario");
        lblMail.setBounds(55, 51, 130, 14);
        addUserPane.add(lblMail);

        JLabel lblRolUs = new JLabel("Rol");
        lblRolUs.setBounds(55, 76, 130, 14);
        addUserPane.add(lblRolUs);

        JTextField textField = new JTextField();
        textField.setBounds(195, 48, 199, 20);
        addUserPane.add(textField);
        textField.setColumns(10);

        JComboBox<String> comboBox_1 = new JComboBox<>();
        comboBox_1.setBounds(195, 73, 199, 20);
        addUserPane.add(comboBox_1);
        comboBox_1.addItem("Lector");
        comboBox_1.addItem("Editor");
        comboBox_1.addItem("Comentador");
        JButton btnPerm = new JButton("Añadir permiso");

        btnPerm.setBounds(266, 145, 141, 23);
        addUserPane.add(btnPerm);

        JCheckBox chckbxNewCheckBox = new JCheckBox("¿Notificar usuario?");
        chckbxNewCheckBox.setBounds(195, 101, 179, 23);
        addUserPane.add(chckbxNewCheckBox);

        JPanel addPanel = new JPanel();
        tabbedPane.addTab("Permisos Dominio/Global", null, addPanel, null);
        addPanel.setLayout(null);

        JLabel lblTipo = new JLabel("Tipo de permiso");
        lblTipo.setBounds(55, 29, 112, 14);
        addPanel.add(lblTipo);

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(177, 26, 230, 20);
        comboBox.addItem("Organización");
        comboBox.addItem("Cualquier usuario");
        addPanel.add(comboBox);

        JLabel lblVis = new JLabel("Visibilidad");
        lblVis.setBounds(55, 57, 112, 14);
        addPanel.add(lblVis);

        JComboBox<String> comboBox_vis = new JComboBox<>();
        comboBox_vis.setBounds(177, 54, 230, 20);
        comboBox_vis.addItem("No");
        comboBox_vis.addItem("Si");
        addPanel.add(comboBox_vis);

        JLabel lblRol = new JLabel("Rol");
        lblRol.setBounds(55, 85, 112, 14);
        addPanel.add(lblRol);

        JComboBox<String> comBox_role = new JComboBox<>();
        comBox_role.setBounds(177, 82, 230, 20);
        comBox_role.addItem("Lector");
        comBox_role.addItem("Editor");
        comBox_role.addItem("Comentador");
        addPanel.add(comBox_role);

        JButton btnAddPerm = new JButton("Añadir permiso");
        btnAddPerm.setBounds(266, 145, 141, 23);
        addPanel.add(btnAddPerm);

        JPanel modPanel = new JPanel();
        tabbedPane.addTab("Modificar Permisos", null, modPanel, null);
        modPanel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        panel_1 = new JPanel();
        scrollPane.setViewportView(panel_1);
        modPanel.add(scrollPane, BorderLayout.CENTER);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onSuccess(Permission permission, HttpHeaders responseHeaders) {
                JOptionPane.showMessageDialog(null, "Modificación realizada correctamente", "Modificación",
                        JOptionPane.INFORMATION_MESSAGE);
                JMain.log.log(Level.INFO, "Modificación OK");
                dispose();
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                JOptionPane.showMessageDialog(null, "Ha surgido un error inesperado", "Modificación",
                        JOptionPane.ERROR_MESSAGE);
                JMain.log.log(Level.SEVERE, "Ha surgido un error\n" + e.getMessage());
                dispose();
            }
        };
        BatchRequest batch = JMainPane.SERVICE.batch();

        btnAddPerm.addActionListener(e -> {
            String role = "";
            Permission p = new Permission();
            boolean vis;
            switch (Objects.requireNonNull(comBox_role.getSelectedItem()).toString()) {
                case "Lector":
                    role = "reader";
                    break;
                case "Editor":
                    role = "writer";
                    break;
                case "Comentador":
                    role = "commenter";
                    break;
            }
            vis = Objects.requireNonNull(comboBox_vis.getSelectedItem()).toString().equals("Si");
            if (Objects.equals(comboBox.getSelectedItem(), "Organización")) {
                p = new Permission().setType("domain").setRole(role).setAllowFileDiscovery(vis)
                        .setDomain("opendeusto.es");
            } else if (Objects.equals(comboBox.getSelectedItem(), "Cualquier usuario")) {
                p = new Permission().setType("anyone").setRole(role).setAllowFileDiscovery(vis);
            }
            try {
                JMainPane.SERVICE.permissions().create(id, p).setFields("id").queue(batch, callback);
                batch.execute();
            } catch (IOException exception) {
                JMain.log.log(Level.SEVERE, exception.getMessage());
            }
        });
        btnPerm.addActionListener(e -> {
            String role = "";

            switch (Objects.requireNonNull(comboBox_1.getSelectedItem()).toString()) {
                case "Lector":
                    role = "reader";
                    break;
                case "Editor":
                    role = "writer";
                    break;
                case "Comentador":
                    role = "commenter";
                    break;
            }
            Permission p = new Permission().setType("user").setRole(role).setEmailAddress(textField.getText());
            try {
                if (chckbxNewCheckBox.isSelected()) {
                    JMainPane.SERVICE.permissions().create(id, p).setFields("id").setSendNotificationEmail(true).queue(batch, callback);
                } else {
                    JMainPane.SERVICE.permissions().create(id, p).setFields("id").queue(batch, callback);
                }
                batch.execute();
            } catch (IOException exception) {
                JMain.log.log(Level.SEVERE, exception.getMessage());
            }
        });
    }


    public void setPermisos(String id) {
        panel_1.removeAll();
        List<Permissions> permissionsList = Database.getFilePermissions(id);
        permissionsList.forEach(permissions -> {
            UserPermissions userPermissions = new UserPermissions(permissions, id, this);
            panel_1.add(userPermissions);
            panel_1.add(Box.createVerticalStrut(SEPARATION));
        });
    }
}
