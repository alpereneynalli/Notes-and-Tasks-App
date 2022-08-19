package tr.project.notesapp.models;

import java.util.Date;

public class Note {

    private String title;
    private String content;
    private String date;
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

    public Note(){
    }

    public Note(String title, String description, String date){
        this.title = title;
        this.content = description;
        this.date = date;
    }
}
