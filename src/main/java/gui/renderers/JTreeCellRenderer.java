package gui.renderers;

import controller.Icons;
import gui.JMain;
import model.DriveFile;
import model.DriveUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Component;

/**
 * Clase se que encarga de renderizar los datos del JTree de la forma apropiada. Para ello se analiza el tipo del
 * nodo, y dependiendo de su clase se realizan distintas acciones
 */
public class JTreeCellRenderer implements TreeCellRenderer {
    private final JLabel label;
    private final JPanel panel;
    private final DefaultTreeCellRenderer defaultTreeCellRenderer = JMain.defaultRenderer;

    public JTreeCellRenderer() {
        label = new JLabel();
        panel = new JPanel();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof DriveFile) {
            DriveFile driveFile = (DriveFile) o;
            ImageIcon icon = Icons.getIcon(driveFile.getMimeType());
            if (driveFile.getMimeType().equals("SharedDrivesFolder") || driveFile.getMimeType().equals(
                    "SharedFilesFolder") || driveFile.getMimeType().equals("MyDriveFolder")) {
                label.setIcon(icon);
            } else if (!leaf && !expanded) {
                label.setIcon(defaultTreeCellRenderer.getClosedIcon());
            } else if (!leaf) {
                label.setIcon(defaultTreeCellRenderer.getOpenIcon());
            } else {
                label.setIcon((icon));
            }
            label.setText(driveFile.getFile_name());
        } else if (o instanceof DriveUnit) {
            DriveUnit driveUnit = (DriveUnit) o;
            if (!leaf && !expanded) {
                label.setIcon(new ImageIcon(getClass().getResource("/img/FileType/DriveUnit_closed.png")));
            } else if (!leaf) {
                label.setIcon(new ImageIcon(getClass().getResource("/img/FileType/DriveUnit_open.png")));
            } else {
                label.setIcon(new ImageIcon(getClass().getResource("/img/FileType/DriveUnit_closed.png")));
            }
            label.setText(driveUnit.getName());
        } else if (o instanceof String) {
            String name = (String) o;
            label.setText(name);
        }
        if (selected) {
            panel.setBackground(defaultTreeCellRenderer.getBackgroundSelectionColor());
        } else {
            panel.setBackground(defaultTreeCellRenderer.getBackgroundNonSelectionColor());
        }
        panel.setOpaque(false);
        panel.add(label);
        return panel;
    }
}
