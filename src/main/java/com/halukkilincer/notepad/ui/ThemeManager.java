package com.halukkilincer.notepad.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.List;

/**
 * Applies consistent Light / Dark / System / High Contrast themes without
 * leaking UIManager overrides between selections.
 */
public final class ThemeManager {

    public enum Theme {
        LIGHT,
        DARK,
        SYSTEM,
        HIGH_CONTRAST
    }

    private static final List<String> MANAGED_KEYS = List.of(
            "control", "info", "window", "text", "menu", "menuText",
            "Panel.background", "Panel.foreground",
            "OptionPane.background", "OptionPane.foreground", "OptionPane.messageForeground",
            "Label.background", "Label.foreground",
            "Button.background", "Button.foreground", "Button.select",
            "ToggleButton.background", "ToggleButton.foreground",
            "ToolBar.background", "ToolBar.foreground", "ToolBar.border",
            "MenuBar.background", "MenuBar.foreground",
            "Menu.background", "Menu.foreground", "Menu.selectionBackground", "Menu.selectionForeground",
            "MenuItem.background", "MenuItem.foreground",
            "MenuItem.selectionBackground", "MenuItem.selectionForeground",
            "CheckBoxMenuItem.background", "CheckBoxMenuItem.foreground",
            "CheckBoxMenuItem.selectionBackground", "CheckBoxMenuItem.selectionForeground",
            "PopupMenu.background", "PopupMenu.foreground",
            "List.background", "List.foreground", "List.selectionBackground", "List.selectionForeground",
            "TextField.background", "TextField.foreground", "TextField.caretForeground",
            "TextField.selectionBackground", "TextField.selectionForeground",
            "TextPane.background", "TextPane.foreground", "TextPane.caretForeground",
            "TextPane.selectionBackground", "TextPane.selectionForeground",
            "EditorPane.background", "EditorPane.foreground",
            "ScrollPane.background", "Viewport.background",
            "SplitPane.background", "SplitPaneDivider.background",
            "ComboBox.background", "ComboBox.foreground",
            "ComboBox.selectionBackground", "ComboBox.selectionForeground",
            "Spinner.background", "Spinner.foreground",
            "Focus.color", "nimbusFocus",
            "nimbusBase", "nimbusAlertYellow", "nimbusDisabledText", "nimbusGreen",
            "nimbusInfoBlue", "nimbusLightBackground", "nimbusOrange", "nimbusRed",
            "nimbusSelectedText", "nimbusSelectionBackground",
            "Label.font", "Button.font", "Menu.font", "MenuItem.font",
            "CheckBoxMenuItem.font", "TextField.font", "TextPane.font", "List.font", "ComboBox.font"
    );

    private ThemeManager() {
    }

    public static void apply(Theme theme, JFrame root) {
        try {
            clearManagedDefaults();

            switch (theme) {
                case LIGHT -> applyLight();
                case DARK -> applyDark();
                case SYSTEM -> applySystem();
                case HIGH_CONTRAST -> applyHighContrast();
            }

            SwingUtilities.updateComponentTreeUI(root);

            if (theme != Theme.SYSTEM) {
                paintTree(root, paletteFor(theme));
            }

            root.repaint();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to apply theme: " + theme, ex);
        }
    }

    public static void applyDefault(JFrame root) {
        apply(Theme.LIGHT, root);
    }

    private static void clearManagedDefaults() {
        for (String key : MANAGED_KEYS) {
            UIManager.getDefaults().remove(key);
        }
    }

    private static void applyLight() throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        putCommon(Palette.light());
    }

    private static void applyDark() throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        putCommon(Palette.dark());
    }

    private static void applySystem() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Font uiFont = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("Menu.font", uiFont);
        UIManager.put("MenuItem.font", uiFont);
        UIManager.put("TextField.font", uiFont);
        UIManager.put("TextPane.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    }

    private static void applyHighContrast() throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        putCommon(Palette.highContrast());
        UIManager.put("Focus.color", Color.YELLOW);
    }

    private static void putCommon(Palette p) {
        Font uiFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        Font editorFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

        UIManager.put("control", p.panel);
        UIManager.put("info", p.panel);
        UIManager.put("window", p.panel);
        UIManager.put("text", p.text);
        UIManager.put("menu", p.menuBackground);
        UIManager.put("menuText", p.menuText);

        UIManager.put("Panel.background", p.panel);
        UIManager.put("Panel.foreground", p.text);
        UIManager.put("OptionPane.background", p.panel);
        UIManager.put("OptionPane.foreground", p.text);
        UIManager.put("OptionPane.messageForeground", p.text);

        UIManager.put("Label.background", p.panel);
        UIManager.put("Label.foreground", p.text);

        UIManager.put("Button.background", p.button);
        UIManager.put("Button.foreground", p.buttonText);
        UIManager.put("Button.select", p.selection);
        UIManager.put("ToggleButton.background", p.button);
        UIManager.put("ToggleButton.foreground", p.buttonText);

        UIManager.put("ToolBar.background", p.toolbar);
        UIManager.put("ToolBar.foreground", p.text);
        UIManager.put("ToolBar.border", new EmptyBorder(4, 4, 4, 4));

        UIManager.put("MenuBar.background", p.menuBackground);
        UIManager.put("MenuBar.foreground", p.menuText);
        UIManager.put("Menu.background", p.menuBackground);
        UIManager.put("Menu.foreground", p.menuText);
        UIManager.put("Menu.selectionBackground", p.selection);
        UIManager.put("Menu.selectionForeground", p.selectionText);
        UIManager.put("MenuItem.background", p.menuBackground);
        UIManager.put("MenuItem.foreground", p.menuText);
        UIManager.put("MenuItem.selectionBackground", p.selection);
        UIManager.put("MenuItem.selectionForeground", p.selectionText);
        UIManager.put("CheckBoxMenuItem.background", p.menuBackground);
        UIManager.put("CheckBoxMenuItem.foreground", p.menuText);
        UIManager.put("CheckBoxMenuItem.selectionBackground", p.selection);
        UIManager.put("CheckBoxMenuItem.selectionForeground", p.selectionText);
        UIManager.put("PopupMenu.background", p.menuBackground);
        UIManager.put("PopupMenu.foreground", p.menuText);

        UIManager.put("List.background", p.editor);
        UIManager.put("List.foreground", p.text);
        UIManager.put("List.selectionBackground", p.selection);
        UIManager.put("List.selectionForeground", p.selectionText);

        UIManager.put("TextField.background", p.editor);
        UIManager.put("TextField.foreground", p.text);
        UIManager.put("TextField.caretForeground", p.text);
        UIManager.put("TextField.selectionBackground", p.selection);
        UIManager.put("TextField.selectionForeground", p.selectionText);

        UIManager.put("TextPane.background", p.editor);
        UIManager.put("TextPane.foreground", p.text);
        UIManager.put("TextPane.caretForeground", p.text);
        UIManager.put("TextPane.selectionBackground", p.selection);
        UIManager.put("TextPane.selectionForeground", p.selectionText);
        UIManager.put("EditorPane.background", p.editor);
        UIManager.put("EditorPane.foreground", p.text);

        UIManager.put("ScrollPane.background", p.panel);
        UIManager.put("Viewport.background", p.editor);
        UIManager.put("SplitPane.background", p.panel);
        UIManager.put("SplitPaneDivider.background", p.button);

        UIManager.put("ComboBox.background", p.button);
        UIManager.put("ComboBox.foreground", p.buttonText);
        UIManager.put("ComboBox.selectionBackground", p.selection);
        UIManager.put("ComboBox.selectionForeground", p.selectionText);
        UIManager.put("Spinner.background", p.button);
        UIManager.put("Spinner.foreground", p.buttonText);

        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("Menu.font", uiFont);
        UIManager.put("MenuItem.font", uiFont);
        UIManager.put("CheckBoxMenuItem.font", uiFont);
        UIManager.put("TextField.font", uiFont);
        UIManager.put("TextPane.font", editorFont);
        UIManager.put("List.font", uiFont);
        UIManager.put("ComboBox.font", uiFont);
    }

    private static Palette paletteFor(Theme theme) {
        return switch (theme) {
            case LIGHT -> Palette.light();
            case DARK -> Palette.dark();
            case HIGH_CONTRAST -> Palette.highContrast();
            case SYSTEM -> Palette.light();
        };
    }

    private static void paintTree(Component component, Palette palette) {
        if (component instanceof JMenuBar menuBar) {
            menuBar.setBackground(palette.menuBackground);
            menuBar.setForeground(palette.menuText);
            menuBar.setOpaque(true);
        }
        if (component instanceof JMenu menu) {
            menu.setBackground(palette.menuBackground);
            menu.setForeground(palette.menuText);
            menu.setOpaque(true);
        }
        if (component instanceof JMenuItem item) {
            item.setBackground(palette.menuBackground);
            item.setForeground(palette.menuText);
            item.setOpaque(true);
        }
        if (component instanceof JToolBar toolBar) {
            toolBar.setBackground(palette.toolbar);
            toolBar.setForeground(palette.text);
            toolBar.setOpaque(true);
            toolBar.setBorder(new EmptyBorder(6, 8, 6, 8));
        }
        if (component instanceof JButton button) {
            button.setFocusPainted(false);
            boolean toolButton = Boolean.TRUE.equals(button.getClientProperty("noteshelf.toolbutton"));
            if (toolButton) {
                button.setForeground(palette.buttonText);
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setBorder(new EmptyBorder(4, 6, 4, 6));
            } else {
                button.setBackground(palette.button);
                button.setForeground(palette.buttonText);
                button.setOpaque(true);
            }
        }
        if (component instanceof JComboBox<?> comboBox) {
            comboBox.setBackground(palette.button);
            comboBox.setForeground(palette.buttonText);
        }
        if (component instanceof JSpinner spinner) {
            spinner.setBackground(palette.button);
            spinner.setForeground(palette.buttonText);
        }
        if (component instanceof JPanel panel) {
            panel.setBackground(palette.panel);
            panel.setForeground(palette.text);
            panel.setOpaque(true);
        }
        if (component instanceof JSplitPane splitPane) {
            splitPane.setBackground(palette.panel);
            splitPane.setOpaque(true);
        }
        if (component instanceof JScrollPane scrollPane) {
            scrollPane.setBackground(palette.panel);
            scrollPane.getViewport().setBackground(palette.editor);
            scrollPane.setBorder(new LineBorder(palette.border));
        }
        if (component instanceof JList<?> list) {
            list.setBackground(palette.editor);
            list.setForeground(palette.text);
            list.setSelectionBackground(palette.selection);
            list.setSelectionForeground(palette.selectionText);
            list.setBorder(new EmptyBorder(6, 6, 6, 6));
        }
        if (component instanceof JTextField field) {
            field.setBackground(palette.editor);
            field.setForeground(palette.text);
            field.setCaretColor(palette.text);
            field.setSelectionColor(palette.selection);
            field.setSelectedTextColor(palette.selectionText);
            field.setBorder(new LineBorder(palette.border));
        }
        if (component instanceof JTextPane pane) {
            pane.setBackground(palette.editor);
            pane.setForeground(palette.text);
            pane.setCaretColor(palette.text);
            pane.setSelectionColor(palette.selection);
            pane.setSelectedTextColor(palette.selectionText);
            pane.setBorder(new EmptyBorder(8, 8, 8, 8));
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                paintTree(child, palette);
            }
            if (component instanceof JMenu menu) {
                for (Component child : menu.getMenuComponents()) {
                    paintTree(child, palette);
                }
            }
        }
    }

    private record Palette(
            Color panel,
            Color editor,
            Color text,
            Color menuBackground,
            Color menuText,
            Color toolbar,
            Color button,
            Color buttonText,
            Color selection,
            Color selectionText,
            Color border
    ) {
        static Palette light() {
            return new Palette(
                    new Color(0xF5F5F7),
                    Color.WHITE,
                    new Color(0x1F2328),
                    new Color(0xF0F0F2),
                    new Color(0x1F2328),
                    new Color(0xE8E8EC),
                    new Color(0xE4E4E8),
                    new Color(0x1F2328),
                    new Color(0x2F6FED),
                    Color.WHITE,
                    new Color(0xD0D0D6)
            );
        }

        static Palette dark() {
            return new Palette(
                    new Color(0x1E1F22),
                    new Color(0x2B2D30),
                    new Color(0xDFE1E5),
                    new Color(0x2B2D30),
                    new Color(0xDFE1E5),
                    new Color(0x2B2D30),
                    new Color(0x3C3F41),
                    new Color(0xDFE1E5),
                    new Color(0x3574F0),
                    Color.WHITE,
                    new Color(0x4A4D51)
            );
        }

        static Palette highContrast() {
            return new Palette(
                    Color.BLACK,
                    Color.BLACK,
                    Color.WHITE,
                    Color.BLACK,
                    Color.WHITE,
                    Color.BLACK,
                    Color.BLACK,
                    Color.WHITE,
                    Color.YELLOW,
                    Color.BLACK,
                    Color.WHITE
            );
        }
    }
}
