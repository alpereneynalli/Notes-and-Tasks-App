package tr.project.notesapp.models;

import java.util.Date;

public class Task {

    private String taskName;
    private String dueDate;
    private Date date;
    private int status;

    public String getTaskName() {
        return taskName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    public Task() {
    }

    public Task(String taskName, String dueDate, int status) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.status = status;
    }
}
