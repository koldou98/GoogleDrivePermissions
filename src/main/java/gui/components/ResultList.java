package gui.components;

import bbdd.Database;
import gui.JMainPane;
import gui.renderers.JListCellRenderer;
import model.DriveFile;
import model.Users;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * Menu desplegable con los elementos encontrados.
 */
public class ResultList extends JPopupMenu {

    private static final long serialVersionUID = -970913890929041522L;

    public ResultList(int width, DefaultListModel<Object> mod, JListCellRenderer jListCellRenderer) {
        setPopupSize(new Dimension(width, 300));
        setFocusable(false);
        setLocation(0, 0);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        add(panel);
        JList<Object> list = new JList<>();
        list.addListSelectionListener(e -> {
            if (list.getSelectedValue() instanceof DriveFile) {
                DriveFile file = ((DriveFile) list.getSelectedValue());
                JMainPane.textSearch.setText(file.getFile_name());
                JMainPane.listModel.clear();
                JMainPane.listModel.addElement(file);
                setVisible(false);
            } else if (list.getSelectedValue() instanceof Users) {
                Users u = (Users) list.getSelectedValue();
                JMainPane.textSearch.setText(u.getDisplayName() + "<" + u.getEmail() + ">");
                JMainPane.listModel.clear();
                List<DriveFile> fileList = Database.getUserFiles(u.getPermissionId());
                fileList.forEach(JMainPane.listModel::addElement);
                setVisible(false);
            }
        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setModel(mod);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setCellRenderer(jListCellRenderer);
        panel.add(scrollPane, BorderLayout.CENTER);
    }
}
