package com.halukkilincer.notepad;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        // Helps Windows distinguish this app from other java.exe taskbar entries.
        System.setProperty("java.awt.Shell.WindowAppUserModelID", "com.halukkilincer.noteshelf");
        NotepadApp.launch();
    }
}
