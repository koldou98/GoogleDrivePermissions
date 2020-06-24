package gui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Font;

/**
 * Clase que se muestra mientras se obtienen los ficheros de Google Drive e inicia la aplicación.
 */
public class JLoad extends JDialog {

    private static final long serialVersionUID = 1952780884360350876L;

    public JLoad() {
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("Cargando programa");
        setVisible(false);
        JPanel panel = new JPanel();
        setContentPane(panel);
        panel.setLayout(null);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(70, 104, 260, 23);
        progressBar.setString("");
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        getContentPane().add(progressBar);
        
        JTextPane txtAreaMsg = new JTextPane();
        txtAreaMsg.setText("Iniciando aplicación.\r\nEsto puede llevar un tiempo...");
        txtAreaMsg.setEnabled(true);
        txtAreaMsg.setEditable(false);
        txtAreaMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txtAreaMsg.setBounds(70, 140, 260, 67);
        StyledDocument doc = txtAreaMsg.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        panel.add(txtAreaMsg);
    }
}
