package notepad.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a Note with a title and content.
 * Immutable, serializable, and null-safe.
 */
public final class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String content;

    /**
     * Constructs a Note object.
     * @param title the title of the note, must not be null
     * @param content the content of the note, must not be null
     * @throws NullPointerException if title or content is null
     */
    public Note(String title, String content) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.content = Objects.requireNonNull(content, "content must not be null");
    }

    /**
     * Gets the title of the note.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the content of the note.
     * @return the content
     */
    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return title.equals(note.title) && content.equals(note.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content);
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}