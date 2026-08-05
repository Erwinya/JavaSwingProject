package com.halukkilincer.notepad;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Product identity for NoteShelf.
 */
public final class AppBranding {

    public static final String APP_NAME = "NoteShelf";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_TAGLINE = "A focused multi-note desktop editor";
    public static final String APP_COPYRIGHT = "(c) 2026 Haluk Kilincer";
    public static final String STORAGE_DIR = ".noteshelf";
    public static final String LEGACY_STORAGE_DIR = ".swing-notepad";
    public static final String ICON_RESOURCE = "/logo-hk.png";

    private static final Logger LOGGER = Logger.getLogger(AppBranding.class.getName());
    private static BufferedImage masterIcon;

    private AppBranding() {
    }

    public static String windowTitle() {
        return APP_NAME;
    }

    public static String aboutText() {
        return APP_NAME + " v" + APP_VERSION + "\n"
                + APP_TAGLINE + "\n"
                + "Built with Java Swing\n"
                + APP_COPYRIGHT;
    }

    public static List<Image> windowIcons() {
        BufferedImage source = loadMasterIcon();
        if (source == null) {
            return List.of();
        }
        int[] sizes = {16, 24, 32, 48, 64, 128, 256};
        List<Image> icons = new ArrayList<>(sizes.length);
        for (int size : sizes) {
            icons.add(scale(source, size));
        }
        return icons;
    }

    public static ImageIcon aboutIcon(int size) {
        BufferedImage source = loadMasterIcon();
        if (source == null) {
            return null;
        }
        return new ImageIcon(scale(source, size));
    }

    private static BufferedImage loadMasterIcon() {
        if (masterIcon != null) {
            return masterIcon;
        }
        try (InputStream in = AppBranding.class.getResourceAsStream(ICON_RESOURCE)) {
            if (in == null) {
                LOGGER.warning("Missing application icon resource: " + ICON_RESOURCE);
                return null;
            }
            masterIcon = ImageIO.read(in);
            return masterIcon;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load application icon", e);
            return null;
        }
    }

    private static BufferedImage scale(BufferedImage source, int size) {
        BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(source.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, size, size, null);
        } finally {
            g.dispose();
        }
        return scaled;
    }
}
