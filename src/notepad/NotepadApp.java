package notepad;

import javax.swing.SwingUtilities;

public class NotepadApp {
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            NotepadFrame frame = new NotepadFrame();
            frame.setVisible(true);
        });
    }
} 