package com.halukkilincer.notepad.ui;

import com.halukkilincer.notepad.model.Note;
import com.halukkilincer.notepad.service.NoteService;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Note editor with a lean, modern formatting toolbar.
 */
public class TextEditorPanel extends JPanel {

    private static final Dimension TOOL_BUTTON_SIZE = new Dimension(32, 28);
    private static final Color ICON_COLOR = new Color(0x333333);
    private static final Pattern BULLET_LINE = Pattern.compile("^(\\s*)([•\\-\\*]\\s+)(.*)$");
    private static final Pattern NUMBER_LINE = Pattern.compile("^(\\s*)(\\d+)\\.\\s+(.*)$");

    private final JTextField titleField = new JTextField();
    private final JTextPane textPane = new JTextPane();
    private final UndoManager undoManager = new UndoManager();
    private Note currentNote;
    private int currentIndex = -1;
    private boolean loading;
    private NoteService noteService;
    private Runnable documentChangeListener;
    private JToolBar toolBar;

    public TextEditorPanel() {
        setLayout(new BorderLayout(0, 0));
        titleField.setFont(titleField.getFont().deriveFont(Font.BOLD, 16f));
        titleField.setBorder(new EmptyBorder(8, 12, 8, 12));
        titleField.setEnabled(false);
        add(titleField, BorderLayout.NORTH);

        toolBar = buildToolbar();
        add(toolBar, BorderLayout.PAGE_START);

        textPane.setBorder(new EmptyBorder(12, 14, 12, 14));
        textPane.setEnabled(false);
        add(new JScrollPane(textPane), BorderLayout.CENTER);

        textPane.getDocument().addUndoableEditListener(undoManager);
        installShortcuts();
        installPersistenceListeners();
    }

    private JToolBar buildToolbar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setRollover(true);
        bar.setBorder(new EmptyBorder(4, 8, 4, 8));
        bar.putClientProperty("noteshelf.toolbar", Boolean.TRUE);

        bar.add(iconButton(ToolbarIcons.undo(ICON_COLOR), "Undo (Ctrl+Z)", e -> undo()));
        bar.add(iconButton(ToolbarIcons.redo(ICON_COLOR), "Redo (Ctrl+Y)", e -> redo()));
        addGap(bar);

        JComboBox<String> fontFamilyBox = new JComboBox<>(availableFonts());
        fontFamilyBox.setSelectedItem(pickDefaultFont(fontFamilyBox));
        fontFamilyBox.setToolTipText("Font");
        fontFamilyBox.setMaximumSize(new Dimension(130, 28));
        fontFamilyBox.setPreferredSize(new Dimension(130, 28));
        fontFamilyBox.addActionListener(e -> setFontFamily((String) fontFamilyBox.getSelectedItem()));
        bar.add(fontFamilyBox);

        JComboBox<Integer> fontSizeBox = new JComboBox<>(new Integer[]{12, 14, 16, 18, 20, 24, 28, 36});
        fontSizeBox.setSelectedItem(14);
        fontSizeBox.setToolTipText("Size");
        fontSizeBox.setMaximumSize(new Dimension(58, 28));
        fontSizeBox.setPreferredSize(new Dimension(58, 28));
        fontSizeBox.addActionListener(e -> setFontSize((Integer) fontSizeBox.getSelectedItem()));
        bar.add(fontSizeBox);
        addGap(bar);

        bar.add(iconButton(ToolbarIcons.bold(ICON_COLOR), "Bold (Ctrl+B)",
                e -> toggleStyle(StyleConstants.CharacterConstants.Bold)));
        bar.add(iconButton(ToolbarIcons.italic(ICON_COLOR), "Italic (Ctrl+I)",
                e -> toggleStyle(StyleConstants.CharacterConstants.Italic)));
        bar.add(iconButton(ToolbarIcons.underline(ICON_COLOR), "Underline (Ctrl+U)",
                e -> toggleStyle(StyleConstants.CharacterConstants.Underline)));
        addGap(bar);

        bar.add(iconButton(ToolbarIcons.textColor(new Color(0xC62828)), "Text color", e -> setColor()));
        bar.add(iconButton(ToolbarIcons.highlight(new Color(0xFFF59D)), "Highlight", e -> setBgColor()));
        addGap(bar);

        bar.add(iconButton(ToolbarIcons.alignLeft(ICON_COLOR), "Align left (Ctrl+L)",
                e -> setAlignment(StyleConstants.ALIGN_LEFT)));
        bar.add(iconButton(ToolbarIcons.alignCenter(ICON_COLOR), "Align center (Ctrl+E)",
                e -> setAlignment(StyleConstants.ALIGN_CENTER)));
        bar.add(iconButton(ToolbarIcons.alignRight(ICON_COLOR), "Align right (Ctrl+R)",
                e -> setAlignment(StyleConstants.ALIGN_RIGHT)));
        addGap(bar);

        bar.add(iconButton(ToolbarIcons.bulletList(ICON_COLOR), "Bullet list (Ctrl+Shift+B)",
                e -> toggleBulletList()));
        bar.add(iconButton(ToolbarIcons.numberedList(ICON_COLOR), "Numbered list",
                e -> toggleNumberedList()));

        return bar;
    }

    private void addGap(JToolBar bar) {
        bar.addSeparator(new Dimension(8, 20));
    }

    private JButton iconButton(Icon icon, String tooltip, ActionListener action) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.setPreferredSize(TOOL_BUTTON_SIZE);
        button.setMinimumSize(TOOL_BUTTON_SIZE);
        button.setMaximumSize(TOOL_BUTTON_SIZE);
        button.setMargin(new Insets(4, 4, 4, 4));
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(false);
        button.putClientProperty("noteshelf.toolbutton", Boolean.TRUE);
        button.addActionListener(action);
        return button;
    }

    private String pickDefaultFont(JComboBox<String> box) {
        for (String candidate : List.of("Segoe UI", "SansSerif", "Dialog")) {
            for (int i = 0; i < box.getItemCount(); i++) {
                if (candidate.equals(box.getItemAt(i))) {
                    return candidate;
                }
            }
        }
        return box.getItemAt(0);
    }

    private String[] availableFonts() {
        Set<String> preferred = new LinkedHashSet<>(Arrays.asList(
                "Segoe UI", "SansSerif", "Serif", "Monospaced",
                "Arial", "Calibri", "Verdana",
                "Times New Roman", "Georgia", "Courier New", "Consolas"
        ));
        Set<String> installed = new LinkedHashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
        ));
        preferred.retainAll(installed);
        if (preferred.isEmpty()) {
            preferred.add("Dialog");
        }
        return preferred.toArray(String[]::new);
    }

    private void installShortcuts() {
        InputMap im = textPane.getInputMap();
        ActionMap am = textPane.getActionMap();
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK),
                "bold", e -> toggleStyle(StyleConstants.CharacterConstants.Bold));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK),
                "italic", e -> toggleStyle(StyleConstants.CharacterConstants.Italic));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK),
                "underline", e -> toggleStyle(StyleConstants.CharacterConstants.Underline));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK),
                "undo", e -> undo());
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK),
                "redo", e -> redo());
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK),
                "alignLeft", e -> setAlignment(StyleConstants.ALIGN_LEFT));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK),
                "alignCenter", e -> setAlignment(StyleConstants.ALIGN_CENTER));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK),
                "alignRight", e -> setAlignment(StyleConstants.ALIGN_RIGHT));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
                "bullet", e -> toggleBulletList());
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "list-aware-enter", e -> handleEnter());
    }

    private void bind(InputMap im, ActionMap am, KeyStroke stroke, String name, ActionListener action) {
        im.put(stroke, name);
        am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
            }
        });
    }

    private void installPersistenceListeners() {
        titleField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                persistCurrentNote();
            }
        });
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                persistCurrentNote();
            }
        });
    }

    private void persistCurrentNote() {
        if (loading || currentNote == null) {
            return;
        }
        currentNote = new Note(titleField.getText(), textPane.getText());
        if (documentChangeListener != null) {
            documentChangeListener.run();
        }
    }

    public Note snapshotNote() {
        if (currentIndex < 0) {
            return null;
        }
        return new Note(titleField.getText(), textPane.getText());
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void bindCurrentNote(Note note, int index) {
        this.currentNote = note;
        this.currentIndex = index;
    }

    private void toggleStyle(Object style) {
        boolean enabled = !Boolean.TRUE.equals(textPane.getInputAttributes().getAttribute(style));
        MutableAttributeSet attr = new SimpleAttributeSet();
        attr.addAttribute(style, enabled);

        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            textPane.getStyledDocument().setCharacterAttributes(start, end - start, attr, false);
        }
        textPane.setCharacterAttributes(attr, false);
        textPane.requestFocusInWindow();
        persistCurrentNote();
    }

    private void setAlignment(int alignment) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setAlignment(attr, alignment);
        int start = textPane.getSelectionStart();
        int length = Math.max(1, textPane.getSelectionEnd() - start);
        doc.setParagraphAttributes(start, length, attr, false);
        textPane.requestFocusInWindow();
        persistCurrentNote();
    }

    public void chooseFont() {
        Font currentFont = textPane.getFont();
        JFontChooser fontChooser = new JFontChooser(currentFont);
        int result = JOptionPane.showConfirmDialog(this, fontChooser, "Choose Font", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Font font = fontChooser.getSelectedFont();
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, font.getFamily());
            StyleConstants.setFontSize(attr, font.getSize());
            applyAttributes(attr, false);
        }
    }

    private void setColor() {
        Color color = JColorChooser.showDialog(this, "Text Color", Color.BLACK);
        if (color != null) {
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, color);
            applyAttributes(attr, false);
        }
    }

    private void setBgColor() {
        Color color = JColorChooser.showDialog(this, "Highlight Color", new Color(0xFFF59D));
        if (color != null) {
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setBackground(attr, color);
            applyAttributes(attr, false);
        }
    }

    private void toggleBulletList() {
        applyListTransform(ListMode.BULLET);
    }

    private void toggleNumberedList() {
        applyListTransform(ListMode.NUMBERED);
    }

    private enum ListMode {
        BULLET,
        NUMBERED
    }

    private void applyListTransform(ListMode mode) {
        try {
            Document doc = textPane.getDocument();
            int selStart = textPane.getSelectionStart();
            int selEnd = Math.max(selStart, textPane.getSelectionEnd());
            int first = lineStart(selStart);
            int last = lineEnd(Math.max(selStart, selEnd - (selStart == selEnd ? 0 : 1)));

            List<int[]> lines = lineRanges(first, last);
            if (lines.isEmpty()) {
                return;
            }

            boolean allMatch = true;
            for (int[] range : lines) {
                String line = doc.getText(range[0], range[1] - range[0]);
                if (mode == ListMode.BULLET && !BULLET_LINE.matcher(line).matches()) {
                    allMatch = false;
                    break;
                }
                if (mode == ListMode.NUMBERED && !NUMBER_LINE.matcher(line).matches()) {
                    allMatch = false;
                    break;
                }
            }

            int caretBias = textPane.getCaretPosition();
            int delta = 0;
            int number = 1;
            for (int[] range : lines) {
                int start = range[0] + delta;
                int end = range[1] + delta;
                String line = doc.getText(start, end - start);
                String rebuilt;
                if (allMatch) {
                    rebuilt = stripListPrefix(line);
                } else if (mode == ListMode.BULLET) {
                    rebuilt = toBullet(line);
                } else {
                    rebuilt = toNumbered(line, number++);
                }
                doc.remove(start, end - start);
                doc.insertString(start, rebuilt, null);
                delta += rebuilt.length() - (end - start);
            }

            int newCaret = Math.min(doc.getLength(), caretBias + delta);
            textPane.setCaretPosition(newCaret);
            textPane.requestFocusInWindow();
            persistCurrentNote();
        } catch (BadLocationException ignored) {
            // ignore
        }
    }

    private String stripListPrefix(String line) {
        Matcher bullet = BULLET_LINE.matcher(line);
        if (bullet.matches()) {
            return bullet.group(1) + bullet.group(3);
        }
        Matcher number = NUMBER_LINE.matcher(line);
        if (number.matches()) {
            return number.group(1) + number.group(3);
        }
        return line;
    }

    private String toBullet(String line) {
        String stripped = stripListPrefix(line);
        Matcher indent = Pattern.compile("^(\\s*)(.*)$").matcher(stripped);
        if (indent.matches()) {
            return indent.group(1) + "• " + indent.group(2);
        }
        return "• " + stripped;
    }

    private String toNumbered(String line, int number) {
        String stripped = stripListPrefix(line);
        Matcher indent = Pattern.compile("^(\\s*)(.*)$").matcher(stripped);
        if (indent.matches()) {
            return indent.group(1) + number + ". " + indent.group(2);
        }
        return number + ". " + stripped;
    }

    private void handleEnter() {
        try {
            Document doc = textPane.getDocument();
            int caret = textPane.getCaretPosition();
            int start = lineStart(caret);
            int end = lineEnd(caret);
            String line = doc.getText(start, end - start);

            Matcher number = NUMBER_LINE.matcher(line);
            if (number.matches()) {
                String content = number.group(3);
                if (content.isBlank()) {
                    doc.remove(start, end - start);
                    return;
                }
                int next = Integer.parseInt(number.group(2)) + 1;
                doc.insertString(caret, "\n" + number.group(1) + next + ". ", null);
                persistCurrentNote();
                return;
            }

            Matcher bullet = BULLET_LINE.matcher(line);
            if (bullet.matches()) {
                String content = bullet.group(3);
                if (content.isBlank()) {
                    doc.remove(start, end - start);
                    return;
                }
                doc.insertString(caret, "\n" + bullet.group(1) + "• ", null);
                persistCurrentNote();
                return;
            }

            doc.insertString(caret, "\n", null);
            persistCurrentNote();
        } catch (BadLocationException ignored) {
            // ignore
        }
    }

    private List<int[]> lineRanges(int from, int to) throws BadLocationException {
        Document doc = textPane.getDocument();
        Element root = doc.getDefaultRootElement();
        int firstIndex = root.getElementIndex(from);
        int lastIndex = root.getElementIndex(Math.max(from, to));
        List<int[]> ranges = new ArrayList<>();
        for (int i = firstIndex; i <= lastIndex; i++) {
            Element el = root.getElement(i);
            int start = el.getStartOffset();
            int end = el.getEndOffset();
            if (end > start) {
                String tail = doc.getText(end - 1, 1);
                if ("\n".equals(tail)) {
                    end--;
                }
            }
            ranges.add(new int[]{start, Math.max(start, end)});
        }
        return ranges;
    }

    private int lineStart(int offset) throws BadLocationException {
        Element root = textPane.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(offset);
        return root.getElement(index).getStartOffset();
    }

    private int lineEnd(int offset) throws BadLocationException {
        Element root = textPane.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(offset);
        Element line = root.getElement(index);
        int end = line.getEndOffset();
        // Exclude trailing newline except for last empty line handling.
        if (end > line.getStartOffset() && end <= textPane.getDocument().getLength()) {
            String tail = textPane.getDocument().getText(end - 1, 1);
            if ("\n".equals(tail)) {
                return end - 1;
            }
        }
        return Math.min(end, textPane.getDocument().getLength());
    }

    private void setFontFamily(String family) {
        if (family == null) {
            return;
        }
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, family);
        applyAttributes(attr, false);
    }

    private void setFontSize(Integer size) {
        if (size == null) {
            return;
        }
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontSize(attr, size);
        applyAttributes(attr, false);
    }

    private void applyAttributes(SimpleAttributeSet attr, boolean replace) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            textPane.getStyledDocument().setCharacterAttributes(start, end - start, attr, replace);
        }
        textPane.setCharacterAttributes(attr, replace);
        textPane.requestFocusInWindow();
        persistCurrentNote();
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void displayNote(Note note) {
        displayNote(note, note == null ? -1 : currentIndex);
    }

    public void displayNote(Note note, int index) {
        loading = true;
        try {
            this.currentNote = note;
            this.currentIndex = index;
            if (note != null) {
                titleField.setText(note.getTitle());
                textPane.setText(note.getContent());
                titleField.setEnabled(true);
                textPane.setEnabled(true);
            } else {
                titleField.setText("");
                textPane.setText("");
                titleField.setEnabled(false);
                textPane.setEnabled(false);
            }
        } finally {
            loading = false;
        }
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public Note getCurrentNote() {
        return currentNote;
    }

    public void addDocumentChangeListener(Runnable listener) {
        this.documentChangeListener = listener;
    }

    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    public void cut() {
        textPane.cut();
    }

    public void copy() {
        textPane.copy();
    }

    public void paste() {
        textPane.paste();
    }

    public void selectAll() {
        textPane.selectAll();
    }

    public void findReplace() {
        String find = JOptionPane.showInputDialog(this, "Find:");
        if (find != null && !find.isEmpty()) {
            String content = textPane.getText();
            int idx = content.indexOf(find);
            if (idx >= 0) {
                textPane.select(idx, idx + find.length());
                String replace = JOptionPane.showInputDialog(this, "Replace with:");
                if (replace != null) {
                    textPane.replaceSelection(replace);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Not found.");
            }
        }
    }

    public void setToolbarVisible(boolean visible) {
        if (toolBar != null) {
            toolBar.setVisible(visible);
            revalidate();
        }
    }

    public void zoomIn() {
        Font f = textPane.getFont();
        textPane.setFont(f.deriveFont(f.getSize2D() + 2f));
    }

    public void zoomOut() {
        Font f = textPane.getFont();
        textPane.setFont(f.deriveFont(Math.max(8f, f.getSize2D() - 2f)));
    }

    public void chooseFontSize() {
        String input = JOptionPane.showInputDialog(this, "Font size:", textPane.getFont().getSize());
        try {
            int size = Integer.parseInt(input);
            textPane.setFont(textPane.getFont().deriveFont((float) size));
        } catch (Exception ignored) {
            // ignore invalid input
        }
    }
}

/** Simple font chooser used from Settings → Font. */
class JFontChooser extends JPanel {
    private final JComboBox<String> fontBox;
    private final JComboBox<Integer> sizeBox;

    JFontChooser(Font initialFont) {
        setLayout(new GridLayout(2, 2, 8, 8));
        fontBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        sizeBox = new JComboBox<>(new Integer[]{8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40});
        add(new JLabel("Font:"));
        add(fontBox);
        add(new JLabel("Size:"));
        add(sizeBox);
        if (initialFont != null) {
            fontBox.setSelectedItem(initialFont.getFamily());
            sizeBox.setSelectedItem(initialFont.getSize());
        }
    }

    public Font getSelectedFont() {
        return new Font((String) fontBox.getSelectedItem(), Font.PLAIN, (Integer) sizeBox.getSelectedItem());
    }
}
