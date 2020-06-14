package org.yinan.core.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author yinan
 * @date 2020/6/13
 */
public class JTextPaneAdapter implements ActionListener {

    private JTextPane jTextPane;

    public JTextPaneAdapter(JTextPane jTextPane) {
        this.jTextPane = jTextPane;
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK, false);
        jTextPane.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
    }

    public JTextPane getTextPane() {
        return jTextPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo("Paste") == 0) {
            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = "";
                try {
                    text = (String)t.getTransferData(DataFlavor.stringFlavor);
                    jTextPane.setText(text);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
