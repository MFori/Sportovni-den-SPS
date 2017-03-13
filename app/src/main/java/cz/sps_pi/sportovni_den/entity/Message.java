package cz.sps_pi.sportovni_den.entity;

import java.util.Date;

/**
 * Created by Martin Forejt on 14.01.2017.
 * forejt.martin97@gmail.com
 */

public class Message {

    private int id;
    private String title;
    private String message;
    private Date date;
    private int sender;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
}
