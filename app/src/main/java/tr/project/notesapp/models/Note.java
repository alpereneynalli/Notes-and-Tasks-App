package tr.project.notesapp.models;

import java.util.Date;

public class Note {

    private String title;
    private String content;
    private String date;
    private String password;
    private boolean passwordBool;
    private Date dateObj;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public Date getDateObj() {
        return dateObj;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPasswordBool() {
        return passwordBool;
    }

    public Note() {
    }

    public Note(String title, String description, String date) {
        this.title = title;
        this.content = description;
        this.date = date;
    }
}
