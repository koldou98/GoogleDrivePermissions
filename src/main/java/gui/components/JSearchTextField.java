package gui.components;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Graphics;
import java.awt.Insets;

/**
 * Componente de la barra de búsqueda. Este componente tiene toda la funcionalidad de un JTextField con la
 * funcionalidad añadida de poder incluir un icono.
 *
 * @see <a href="https://stackoverflow.com/questions/6089410/decorating-a-jtextfield-with-an-image-and-hint">
 * * https://stackoverflow.com/questions/6089410/decorating-a-jtextfield-with-an-image-and-hint</a>
 */
public class JSearchTextField extends JTextField {

    private static final long serialVersionUID = 3970568318852127895L;
    private static final int ICON_SPACING = 4;
    private Border mBorder;
    private Icon mIcon;

    /**
     * Icono que se va a introducir en el componente
     *
     * @param icon Icono que se va a establecer
     */
    public void setIcon(Icon icon) {
        mIcon = icon;
        resetBorder();
    }

    /**
     * Se actualiza el tamaño del borde
     *
     * @param border Parámetro de tipo Border <code>{@link Border}</code>
     */
    @Override
    public void setBorder(Border border) {
        mBorder = border;

        if (mIcon == null) {
            super.setBorder(border);
        } else {
            Border margin = BorderFactory.createEmptyBorder(0, 0, 0, mIcon.getIconWidth() + ICON_SPACING);
            Border compound = BorderFactory.createCompoundBorder(border, margin);
            super.setBorder(compound);
        }
    }

    /**
     * Se repinta el componente con el icono si existe
     *
     * @param graphics clase de gráficos de tipo <code>{@link Graphics}</code>
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (mIcon != null) {
            Insets iconInsets = mBorder.getBorderInsets(this);
            mIcon.paintIcon(this, graphics, this.getWidth() - mIcon.getIconWidth() - 2, iconInsets.top);
        }
    }

    /**
     * Establece el nuevo borde del componente
     */
    private void resetBorder() {
        setBorder(mBorder);
    }
}
