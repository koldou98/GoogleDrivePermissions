package gui.renderers;

import controller.Icons;
import gui.JMain;
import model.DriveFile;
import model.DriveUnit;
import model.Users;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Clase se que encarga de renderizar los datos del JList de la forma apropiada. Para ello se analiza el tipo del
 * fichero, y dependiendo de su clase se realizan distintas acciones
 */
public class JListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -4942662378738182849L;
	
	private final JLabel label;
    private final JPanel panel;

    public JListCellRenderer() {
        label = new JLabel();
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean selected,
            boolean expanded) {
        if (value instanceof DriveFile) {
            DriveFile driveFile = (DriveFile) value;
            if(driveFile.getMimeType().equals("application/vnd.google-apps.folder")){
             label.setIcon(JMain.defaultRenderer.getClosedIcon());
            }else {
                ImageIcon icon = Icons.getIcon(driveFile.getMimeType());
            label.setIcon(icon);
            }
            label.setText(driveFile.getFile_name());
        } else if (value instanceof DriveUnit) {
            DriveUnit driveUnit = (DriveUnit) value;
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/FileType/DriveUnit_closed.png"));
            label.setIcon(icon);
            label.setText(driveUnit.getName());
        } else if(value instanceof Users){
            Users user = (Users) value;
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/FileType/user.png"));
            label.setIcon(icon);
            label.setText(user.getDisplayName()+"<"+user.getEmail()+">");
        }
        if (selected) {
            panel.setBackground(list.getSelectionBackground());
        } else {
            panel.setBackground(null);
        }
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

}
