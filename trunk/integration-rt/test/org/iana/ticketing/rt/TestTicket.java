package org.iana.ticketing.rt;

import org.iana.ticketing.Ticket;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TestTicket implements Ticket {
    private Long id;
    private String tld;
    private List<String> requestType;
    private String ianaState;
    private String comment;

    public TestTicket(String tld) {
        this.tld = tld;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTld(String tld) {
        this.tld = tld;
    }

    public void setRequestType(List<String> requestType) {
        this.requestType = requestType;
    }

    public void setIanaState(String ianaState) {
        this.ianaState = ianaState;
    }

    public Long getId() {
        return id;
    }

    public String getTld() {
        return tld;
    }

    public List<String> getRequestType() {
        return requestType;
    }

    public String getIanaState() {
        return ianaState;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isNSGlueTicket() {
        return false;
    }

    public String getCurrentStateComment() {
        return "";
    }
}
