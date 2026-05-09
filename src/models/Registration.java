package models;

import java.sql.Date;

public class Registration {
    private String regId;
    private Date regDate;
    private String studentId;
    private String eventId;

    // Full Constructor
    public Registration(String regId, Date regDate, String studentId, String eventId) {
        this.regId = regId;
        this.regDate = regDate;
        this.studentId = studentId;
        this.eventId = eventId;
    }

    // Getters
    public String getRegId() { return regId; }
    public Date getRegDate() { return regDate; }
    public String getStudentId() { return studentId; }
    public String getEventId() { return eventId; }

    // Setters
    public void setRegId(String regId) { this.regId = regId; }
    public void setRegDate(Date regDate) { this.regDate = regDate; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}