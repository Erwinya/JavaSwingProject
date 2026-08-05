package com.halukkilincer.notepad.ui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 * Lightweight vector icons for the editor toolbar (no external assets).
 */
final class ToolbarIcons {

    private ToolbarIcons() {
    }

    static Icon undo(Color color) {
        return new VectorIcon(18, color, (g, c) -> {
            g.drawArc(4, 5, 10, 10, 45, 200);
            g.fillPolygon(new int[]{3, 7, 7}, new int[]{6, 3, 9}, 3);
        });
    }

    static Icon redo(Color color) {
        return new VectorIcon(18, color, (g, c) -> {
            g.drawArc(4, 5, 10, 10, -45, -200);
            g.fillPolygon(new int[]{15, 11, 11}, new int[]{6, 3, 9}, 3);
        });
    }

    static Icon bold(Color color) {
        return new TextIcon("B", color, true, false);
    }

    static Icon italic(Color color) {
        return new TextIcon("I", color, false, true);
    }

    static Icon underline(Color color) {
        return new VectorIcon(18, color, (g, c) -> {
            g.setFont(g.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
            g.drawString("U", 5, 13);
            g.drawLine(4, 15, 14, 15);
        });
    }

    static Icon textColor(Color accent) {
        return new VectorIcon(18, accent, (g, c) -> {
            g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 12f));
            g.drawString("A", 5, 12);
            g.setColor(accent);
            g.fillRect(3, 14, 12, 3);
        });
    }

    static Icon highlight(Color accent) {
        return new VectorIcon(18, new Color(0x333333), (g, c) -> {
            g.setColor(accent);
            g.fillRoundRect(2, 3, 14, 12, 3, 3);
            g.setColor(new Color(0x333333));
            g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 11f));
            g.drawString("A", 5, 13);
        });
    }

    static Icon alignLeft(Color color) {
        return align(color, 0);
    }

    static Icon alignCenter(Color color) {
        return align(color, 1);
    }

    static Icon alignRight(Color color) {
        return align(color, 2);
    }

    private static Icon align(Color color, int mode) {
        return new VectorIcon(18, color, (g, c) -> {
            int[] widths = {12, 8, 10, 7};
            for (int i = 0; i < widths.length; i++) {
                int y = 4 + i * 3;
                int w = widths[i];
                int x = switch (mode) {
                    case 1 -> (18 - w) / 2;
                    case 2 -> 18 - 3 - w;
                    default -> 3;
                };
                g.fillRect(x, y, w, 2);
            }
        });
    }

    static Icon bulletList(Color color) {
        return new VectorIcon(18, color, (g, c) -> {
            for (int i = 0; i < 3; i++) {
                int y = 4 + i * 4;
                g.fillOval(3, y, 3, 3);
                g.fillRect(8, y + 1, 7, 2);
            }
        });
    }

    static Icon numberedList(Color color) {
        return new VectorIcon(18, color, (g, c) -> {
            g.setFont(g.getFont().deriveFont(java.awt.Font.PLAIN, 8f));
            g.drawString("1", 2, 8);
            g.drawString("2", 2, 13);
            g.drawString("3", 2, 18);
            g.fillRect(9, 5, 6, 2);
            g.fillRect(9, 10, 6, 2);
            g.fillRect(9, 15, 6, 2);
        });
    }

    @FunctionalInterface
    private interface Painter {
        void paint(Graphics2D g, Color color);
    }

    private static final class VectorIcon implements Icon {
        private final int size;
        private final Color color;
        private final Painter painter;

        private VectorIcon(int size, Color color, Painter painter) {
            this.size = size;
            this.color = color;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.translate(x, y);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                Color paint = color;
                if (c != null && c.getForeground() != null && color.getRGB() == new Color(0x333333).getRGB()) {
                    paint = c.getForeground();
                }
                g2.setColor(paint);
                Stroke old = g2.getStroke();
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                painter.paint(g2, paint);
                g2.setStroke(old);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    private static final class TextIcon implements Icon {
        private final String text;
        private final Color color;
        private final boolean bold;
        private final boolean italic;

        private TextIcon(String text, Color color, boolean bold, boolean italic) {
            this.text = text;
            this.color = color;
            this.bold = bold;
            this.italic = italic;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int style = fontStyle(bold, italic);
                Color paint = c != null && c.getForeground() != null ? c.getForeground() : color;
                g2.setColor(paint);
                g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, style, 13));
                g2.drawString(text, x + 4, y + 14);
            } finally {
                g2.dispose();
            }
        }

        private static int fontStyle(boolean bold, boolean italic) {
            int style = java.awt.Font.PLAIN;
            if (bold) {
                style |= java.awt.Font.BOLD;
            }
            if (italic) {
                style |= java.awt.Font.ITALIC;
            }
            return style;
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }
    }
}
