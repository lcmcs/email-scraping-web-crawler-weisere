package edu.touro.cs.mcon364;

import java.time.LocalDateTime;

public class Email {
    private int emailID;
    private String emailAddress;
    private String source;
    private LocalDateTime timeStamp;

    public Email(String e, String source, LocalDateTime time){
        emailAddress = e;
        this.source = source;
        timeStamp = time;
    }

    public int getEmailID() {
        return emailID;
    }

    public void setEmailID(int emailID) {
        this.emailID = emailID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}

