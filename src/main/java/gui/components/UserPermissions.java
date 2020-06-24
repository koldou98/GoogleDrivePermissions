package gui.components;

import com.google.api.services.drive.model.Permission;
import gui.JMain;
import gui.JMainPane;
import gui.JPermissions;
import model.Permissions;

import javax.swing.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Componente para modificación y eliminación de permisos existentes.
 */
public class UserPermissions extends JComponent {
    /**
     *
     */
    private static final long serialVersionUID = 6685351037494440066L;

    public UserPermissions(Permissions p, String id, JPermissions jPermissions) {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{25, 172, 67, 184, 25, 0};
        gridBagLayout.rowHeights = new int[]{23, 23, 23, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);

        Component horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
        gbc_horizontalStrut.gridx = 0;
        gbc_horizontalStrut.gridy = 0;
        add(horizontalStrut, gbc_horizontalStrut);

        JLabel lblNombre = new JLabel();
        if (p.getUser().getDisplayName() == null) {
            switch (p.getUser().getPermissionId()) {
                case "anyone":
                    lblNombre.setText("Cualquiera");
                    break;
                case "anyoneWithLink":
                    lblNombre.setText("Cualquiera con enlace");
                    break;
            }
        } else if (p.getUser().getDisplayName().equals("myOpenDeusto") && p.getUser().getEmail() == null) {
            lblNombre.setText("Dominio");
        } else {
            lblNombre.setText(p.getUser().getDisplayName() + "<" + p.getUser().getEmail() + ">");
        }
        GridBagConstraints gbc_lblNombre = new GridBagConstraints();
        gbc_lblNombre.fill = GridBagConstraints.BOTH;
        gbc_lblNombre.insets = new Insets(0, 0, 5, 5);
        gbc_lblNombre.gridwidth = 3;
        gbc_lblNombre.gridx = 1;
        gbc_lblNombre.gridy = 0;
        add(lblNombre, gbc_lblNombre);

        Component horizontalStrut_1 = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
        gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 0);
        gbc_horizontalStrut_1.gridx = 4;
        gbc_horizontalStrut_1.gridy = 0;
        add(horizontalStrut_1, gbc_horizontalStrut_1);

        JLabel lblTipo = new JLabel();
        lblTipo.setText("Tipo de permiso");
        GridBagConstraints gbc_lblTipo = new GridBagConstraints();
        gbc_lblTipo.fill = GridBagConstraints.BOTH;
        gbc_lblTipo.insets = new Insets(0, 0, 5, 5);
        gbc_lblTipo.gridx = 1;
        gbc_lblTipo.gridy = 1;
        add(lblTipo, gbc_lblTipo);

        JComboBox<String> comboBox = new JComboBox<>();
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.fill = GridBagConstraints.BOTH;
        gbc_comboBox.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox.gridx = 3;
        gbc_comboBox.gridy = 1;
        add(comboBox, gbc_comboBox);

        JButton btnNewButton = new JButton("Eliminar Permiso");
        btnNewButton.addActionListener(e -> {
            try {
                JMainPane.SERVICE.permissions().delete(id, p.getUser().getPermissionId()).execute();
                JOptionPane.showMessageDialog(null, "Permiso eliminado correctamente", "Eliminación",
                        JOptionPane.INFORMATION_MESSAGE);
                JMain.log.log(Level.FINE, "Permiso eliminado");

                jPermissions.dispose();
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(null, "Ha surgido un error inesperado", "Eliminación",
                        JOptionPane.ERROR_MESSAGE);
                JMain.log.log(Level.SEVERE, exception.getMessage());
            }
        });
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.fill = GridBagConstraints.BOTH;
        gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
        gbc_btnNewButton.gridx = 1;
        gbc_btnNewButton.gridy = 2;
        add(btnNewButton, gbc_btnNewButton);

        JButton btnNewButton_1 = new JButton("Actualizar permiso");
        GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
        gbc_btnNewButton_1.insets = new Insets(0, 0, 0, 5);
        gbc_btnNewButton_1.fill = GridBagConstraints.BOTH;
        gbc_btnNewButton_1.gridx = 3;
        gbc_btnNewButton_1.gridy = 2;
        add(btnNewButton_1, gbc_btnNewButton_1);
        switch (p.getRole()) {
            case "owner":
                comboBox.addItem("Propietario");
                comboBox.setEnabled(false);
                btnNewButton.setEnabled(false);
                btnNewButton_1.setEnabled(false);
                break;
            case "writer":
                comboBox.addItem("Editor");
                comboBox.addItem("Lector");
                comboBox.addItem("Comentarista");
                break;
            case "reader":
                comboBox.addItem("Lector");
                comboBox.addItem("Editor");
                comboBox.addItem("Comentarista");
                break;
            case "commenter":
                comboBox.addItem("Comentarista");
                comboBox.addItem("Editor");
                comboBox.addItem("Lector");
                break;
        }
        btnNewButton_1.addActionListener(e -> {
            String role = "";
            Permission permission;
            switch (Objects.requireNonNull(comboBox.getSelectedItem()).toString()) {
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
            if (lblNombre.getText().equals("Dominio")) {
                permission = new Permission().setRole(role).setType("domain").setDomain("opendeusto.es");
            } else if (lblNombre.getText().equals("Cualquiera") || lblNombre.getText().equals("Cualquiera con enlace")) {
                permission = new Permission().setRole(role).setType("anyone");
            } else {
                permission = new Permission().setRole(role).setType("user").setEmailAddress(p.getUser().getEmail());
            }
            try {
                JMainPane.SERVICE.permissions().create(id, permission).execute();
                JOptionPane.showMessageDialog(null, "Modificación realizada correctamente", "Modificación",
                        JOptionPane.INFORMATION_MESSAGE);
                JMain.log.log(Level.INFO, "Modificación OK");
                jPermissions.dispose();
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(null, "Ha surgido un error inesperado", "Modificación",
                        JOptionPane.ERROR_MESSAGE);
                JMain.log.log(Level.SEVERE, exception.getMessage());
                jPermissions.dispose();
            }
        });
    }
}
