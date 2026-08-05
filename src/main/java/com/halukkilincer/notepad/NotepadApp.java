package com.halukkilincer.notepad;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Image;
import java.awt.Taskbar;
import java.util.List;

public final class NotepadApp {
    private NotepadApp() {
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to default LAF
            }

            applyTaskbarIcon();

            NotepadFrame frame = new NotepadFrame();
            frame.setVisible(true);
            applyTaskbarIcon();
        });
    }

    private static void applyTaskbarIcon() {
        List<Image> icons = AppBranding.windowIcons();
        if (icons.isEmpty()) {
            return;
        }
        Image primary = icons.get(Math.min(2, icons.size() - 1)); // prefer 32px-ish
        if (Taskbar.isTaskbarSupported()) {
            try {
                Taskbar.getTaskbar().setIconImage(primary);
            } catch (UnsupportedOperationException | SecurityException ignored) {
                // platform may deny taskbar icon changes
            }
        }
    }
}
