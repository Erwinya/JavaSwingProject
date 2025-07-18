package notepad.ui;

import notepad.model.Note;
import notepad.service.NoteService;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;

public class TextEditorPanel extends JPanel {
    private final JTextField titleField = new JTextField();
    private final JTextPane textPane = new JTextPane();
    private final UndoManager undoManager = new UndoManager();
    private Note currentNote;
    private NoteService noteService;
    private Runnable documentChangeListener;
    private JToolBar toolBar;

    public TextEditorPanel() {
        setLayout(new BorderLayout());
        titleField.setFont(titleField.getFont().deriveFont(Font.BOLD, 16f));
        add(titleField, BorderLayout.NORTH);
        titleField.setEnabled(false);

        // Toolbar
        toolBar = new JToolBar();
        // Styles
        String[] styles = {"Normal", "Title", "Subtitle", "Heading 1", "Heading 2", "Heading 3", "Quote", "Code", "Highlight"};
        JComboBox<String> styleBox = new JComboBox<>(styles);
        toolBar.add(styleBox);
        // Font families
        String[] fontFamilies = {"System Default", "Arial", "Calibri", "Segoe UI", "Verdana", "Roboto", "Open Sans", "Times New Roman", "Georgia", "Courier New", "Consolas"};
        JComboBox<String> fontFamilyBox = new JComboBox<>(fontFamilies);
        toolBar.add(fontFamilyBox);
        // Font sizes
        Integer[] fontSizes = {10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 48, 72};
        JComboBox<Integer> fontSizeBox = new JComboBox<>(fontSizes);
        toolBar.add(fontSizeBox);
        JButton boldBtn = new JButton("B");
        boldBtn.setFont(boldBtn.getFont().deriveFont(Font.BOLD));
        JButton italicBtn = new JButton("I");
        italicBtn.setFont(italicBtn.getFont().deriveFont(Font.ITALIC));
        JButton underlineBtn = new JButton("U");
        JButton strikeBtn = new JButton("S");
        strikeBtn.setFont(strikeBtn.getFont().deriveFont(Font.PLAIN));
        JButton fontBtn = new JButton("Font");
        JButton colorBtn = new JButton("A");
        colorBtn.setForeground(Color.RED);
        JButton bgColorBtn = new JButton("A");
        bgColorBtn.setBackground(Color.YELLOW);
        JButton clearBtn = new JButton("✖");
        JButton leftBtn = new JButton("L");
        JButton centerBtn = new JButton("C");
        JButton rightBtn = new JButton("R");
        JButton numListBtn = new JButton("1.");
        JButton bulletBtn = new JButton("•");
        JButton undoBtn = new JButton("Undo");
        JButton redoBtn = new JButton("Redo");
        JButton linkBtn = new JButton("🔗");
        JButton imageBtn = new JButton("🖼");
        JButton tableBtn = new JButton("Tbl");
        JButton codeBtn = new JButton("<>");
        JButton quoteBtn = new JButton("❝");

        toolBar.addSeparator();
        toolBar.add(boldBtn);
        toolBar.add(italicBtn);
        toolBar.add(underlineBtn);
        toolBar.add(strikeBtn);
        toolBar.add(fontBtn);
        toolBar.add(colorBtn);
        toolBar.add(bgColorBtn);
        toolBar.add(clearBtn);
        toolBar.addSeparator();
        toolBar.add(leftBtn);
        toolBar.add(centerBtn);
        toolBar.add(rightBtn);
        toolBar.addSeparator();
        toolBar.add(numListBtn);
        toolBar.add(bulletBtn);
        toolBar.addSeparator();
        toolBar.add(undoBtn);
        toolBar.add(redoBtn);
        toolBar.addSeparator();
        toolBar.add(linkBtn);
        toolBar.add(imageBtn);
        toolBar.add(tableBtn);
        toolBar.add(codeBtn);
        toolBar.add(quoteBtn);
        add(toolBar, BorderLayout.PAGE_START);

        add(new JScrollPane(textPane), BorderLayout.CENTER);
        textPane.setEnabled(false);

        // Undo/Redo
        textPane.getDocument().addUndoableEditListener(undoManager);
        undoBtn.addActionListener(e -> undo());
        redoBtn.addActionListener(e -> redo());

        // Formatting
        boldBtn.addActionListener(e -> setStyle(StyleConstants.CharacterConstants.Bold));
        italicBtn.addActionListener(e -> setStyle(StyleConstants.CharacterConstants.Italic));
        underlineBtn.addActionListener(e -> setStyle(StyleConstants.CharacterConstants.Underline));
        strikeBtn.addActionListener(e -> setStyle(StyleConstants.CharacterConstants.StrikeThrough));
        fontBtn.addActionListener(e -> chooseFont());
        colorBtn.addActionListener(e -> setColor());
        bgColorBtn.addActionListener(e -> setBgColor());
        clearBtn.addActionListener(e -> clearFormatting());
        leftBtn.addActionListener(e -> setAlignment(StyleConstants.ALIGN_LEFT));
        centerBtn.addActionListener(e -> setAlignment(StyleConstants.ALIGN_CENTER));
        rightBtn.addActionListener(e -> setAlignment(StyleConstants.ALIGN_RIGHT));
        numListBtn.addActionListener(e -> insertNumberedList());
        bulletBtn.addActionListener(e -> insertBullet());
        linkBtn.addActionListener(e -> insertLink());
        imageBtn.addActionListener(e -> insertImage());
        tableBtn.addActionListener(e -> insertTable());
        codeBtn.addActionListener(e -> applyStyle("Code"));
        quoteBtn.addActionListener(e -> applyStyle("Quote"));
        styleBox.addActionListener(e -> applyStyle((String) styleBox.getSelectedItem()));
        fontFamilyBox.addActionListener(e -> setFontFamily((String) fontFamilyBox.getSelectedItem()));
        fontSizeBox.addActionListener(e -> setFontSize((Integer) fontSizeBox.getSelectedItem()));

        // Keyboard shortcuts
        InputMap im = textPane.getInputMap();
        ActionMap am = textPane.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "bold");
        am.put("bold", new AbstractAction() { public void actionPerformed(ActionEvent e) { setStyle(StyleConstants.CharacterConstants.Bold); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), "italic");
        am.put("italic", new AbstractAction() { public void actionPerformed(ActionEvent e) { setStyle(StyleConstants.CharacterConstants.Italic); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK), "underline");
        am.put("underline", new AbstractAction() { public void actionPerformed(ActionEvent e) { setStyle(StyleConstants.CharacterConstants.Underline); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        am.put("undo", new AbstractAction() { public void actionPerformed(ActionEvent e) { undo(); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        am.put("redo", new AbstractAction() { public void actionPerformed(ActionEvent e) { redo(); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "alignLeft");
        am.put("alignLeft", new AbstractAction() { public void actionPerformed(ActionEvent e) { setAlignment(StyleConstants.ALIGN_LEFT); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "alignCenter");
        am.put("alignCenter", new AbstractAction() { public void actionPerformed(ActionEvent e) { setAlignment(StyleConstants.ALIGN_CENTER); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "alignRight");
        am.put("alignRight", new AbstractAction() { public void actionPerformed(ActionEvent e) { setAlignment(StyleConstants.ALIGN_RIGHT); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "bullet");
        am.put("bullet", new AbstractAction() { public void actionPerformed(ActionEvent e) { insertBullet(); }});

        titleField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (currentNote != null) {
                    currentNote.setTitle(titleField.getText());
                    if (documentChangeListener != null) documentChangeListener.run();
                }
            }
        });
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (currentNote != null) {
                    currentNote.setContent(textPane.getText());
                    if (documentChangeListener != null) documentChangeListener.run();
                }
            }
        });
    }

    private void setStyle(Object style) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start == end) return;
        MutableAttributeSet attr = new SimpleAttributeSet();
        attr.addAttribute(style, Boolean.TRUE);
        doc.setCharacterAttributes(start, end - start, attr, false);
    }

    private void setAlignment(int alignment) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setAlignment(attr, alignment);
        doc.setParagraphAttributes(textPane.getSelectionStart(), textPane.getSelectionEnd() - textPane.getSelectionStart(), attr, false);
    }

    public void chooseFont() { chooseFontInternal(); }
    private void chooseFontInternal() {
        Font currentFont = textPane.getFont();
        JFontChooser fontChooser = new JFontChooser(currentFont);
        int result = JOptionPane.showConfirmDialog(this, fontChooser, "Choose Font", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Font font = fontChooser.getSelectedFont();
            textPane.setFont(font);
        }
    }

    private void setColor() {
        Color color = JColorChooser.showDialog(this, "Text Color", textPane.getForeground());
        if (color != null) {
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, color);
            textPane.setCharacterAttributes(attr, false);
        }
    }
    private void setBgColor() {
        Color color = JColorChooser.showDialog(this, "Background Color", textPane.getBackground());
        if (color != null) {
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setBackground(attr, color);
            textPane.setCharacterAttributes(attr, false);
        }
    }
    private void clearFormatting() {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        textPane.setCharacterAttributes(attr, true);
    }
    private void insertNumberedList() {
        try {
            textPane.getDocument().insertString(textPane.getCaretPosition(), "1. ", null);
        } catch (BadLocationException ignored) {}
    }
    private void insertBullet() {
        try {
            textPane.getDocument().insertString(textPane.getCaretPosition(), "\u2022 ", null);
        } catch (BadLocationException ignored) {}
    }
    private void insertLink() {
        String url = JOptionPane.showInputDialog(this, "Enter URL:");
        if (url != null && !url.isEmpty()) {
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, Color.BLUE);
            StyleConstants.setUnderline(attr, true);
            textPane.replaceSelection(url);
            textPane.setCharacterAttributes(attr, false);
        }
    }
    private void insertImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                textPane.insertIcon(new ImageIcon(chooser.getSelectedFile().getAbsolutePath()));
            } catch (Exception ignored) {}
        }
    }
    private void insertTable() {
        try {
            textPane.getDocument().insertString(textPane.getCaretPosition(), "|   |   |   |\n|---|---|---|\n|   |   |   |\n", null);
        } catch (BadLocationException ignored) {}
    }
    private void applyStyle(String style) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        switch (style) {
            case "Title":
                StyleConstants.setFontSize(attr, 28);
                StyleConstants.setBold(attr, true);
                break;
            case "Subtitle":
                StyleConstants.setFontSize(attr, 20);
                StyleConstants.setItalic(attr, true);
                break;
            case "Heading 1":
                StyleConstants.setFontSize(attr, 24);
                StyleConstants.setBold(attr, true);
                break;
            case "Heading 2":
                StyleConstants.setFontSize(attr, 18);
                StyleConstants.setBold(attr, true);
                break;
            case "Heading 3":
                StyleConstants.setFontSize(attr, 14);
                StyleConstants.setBold(attr, true);
                break;
            case "Quote":
                StyleConstants.setItalic(attr, true);
                StyleConstants.setForeground(attr, Color.GRAY);
                break;
            case "Code":
                StyleConstants.setFontFamily(attr, "Consolas");
                StyleConstants.setBackground(attr, new Color(230,230,230));
                break;
            case "Highlight":
                StyleConstants.setBackground(attr, Color.YELLOW);
                break;
            default:
                StyleConstants.setFontSize(attr, 12);
                StyleConstants.setBold(attr, false);
                StyleConstants.setItalic(attr, false);
                StyleConstants.setFontFamily(attr, "Dialog");
        }
        textPane.setCharacterAttributes(attr, true);
    }
    private void setFontFamily(String family) {
        if (family == null) return;
        if (family.equals("System Default")) family = "Dialog";
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, family);
        textPane.setCharacterAttributes(attr, false);
    }
    private void setFontSize(Integer size) {
        if (size == null) return;
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontSize(attr, size);
        textPane.setCharacterAttributes(attr, false);
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void displayNote(Note note) {
        this.currentNote = note;
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
    }

    public JTextPane getTextPane() { return textPane; }
    public Note getCurrentNote() { return currentNote; }
    public void addDocumentChangeListener(Runnable listener) { this.documentChangeListener = listener; }
    public void undo() { if (undoManager.canUndo()) undoManager.undo(); }
    public void redo() { if (undoManager.canRedo()) undoManager.redo(); }
    public void cut() { textPane.cut(); }
    public void copy() { textPane.copy(); }
    public void paste() { textPane.paste(); }
    public void selectAll() { textPane.selectAll(); }
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
    public void setToolbarVisible(boolean visible) { if (toolBar != null) toolBar.setVisible(visible); }
    public void zoomIn() { Font f = textPane.getFont(); textPane.setFont(f.deriveFont(f.getSize2D() + 2f)); }
    public void zoomOut() { Font f = textPane.getFont(); textPane.setFont(f.deriveFont(Math.max(8f, f.getSize2D() - 2f))); }
    public void chooseFontSize() {
        String input = JOptionPane.showInputDialog(this, "Font size:", textPane.getFont().getSize());
        try {
            int size = Integer.parseInt(input);
            textPane.setFont(textPane.getFont().deriveFont((float)size));
        } catch (Exception ignored) {}
    }
}

// Simple font chooser for demonstration
class JFontChooser extends JPanel {
    private final JComboBox<String> fontBox;
    private final JComboBox<Integer> sizeBox;
    public JFontChooser(Font initialFont) {
        setLayout(new GridLayout(2, 2));
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