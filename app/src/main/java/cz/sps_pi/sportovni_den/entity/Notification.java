package cz.sps_pi.sportovni_den.entity;

/**
 * Created by Martin Forejt on 14.01.2017.
 * forejt.martin97@gmail.com
 */

public class Notification extends Message {

    private int addressee;
    private int sender;

    public int getAddressee() {
        return addressee;
    }

    public void setAddressee(int addressee) {
        this.addressee = addressee;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
}
