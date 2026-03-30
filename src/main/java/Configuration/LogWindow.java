package Configuration;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LogWindow {

    private static final JTextPane textPane = new JTextPane();
    private static final StyledDocument doc = textPane.getStyledDocument();

    static {
        JFrame frame = new JFrame("Logs");
        textPane.setEditable(false);

        frame.add(new JScrollPane(textPane), BorderLayout.CENTER);
        frame.setSize(800, 500);
        frame.setVisible(true);
        textPane.setBackground(Color.black);
    }

    public static void append(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            Style style = textPane.addStyle("style", null);
            StyleConstants.setForeground(style, color);

            try {
                doc.insertString(doc.getLength(), text, style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            textPane.setCaretPosition(doc.getLength()); // auto-scroll
        });
    }
}