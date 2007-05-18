package org.iana.rzm.mail.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class ConfirmationMailData extends AbstractMailData{
    private Long ticketId;
    private String stateName;
    private boolean accepted;

    public ConfirmationMailData() {
    }

    public ConfirmationMailData(String originalSubject, String originalBody) {
        super(originalSubject, originalBody);
    }

    public ConfirmationMailData(Long ticketId, String stateName, boolean accepted) {
        this.ticketId = ticketId;
        this.stateName = stateName;
        this.accepted = accepted;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
