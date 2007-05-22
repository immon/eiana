package org.iana.rzm.mail.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class ConfirmationMailData extends AbstractMailData{
    private Long transactionId;
    private String stateName;
    private boolean accepted;

    public ConfirmationMailData() {
    }

    public ConfirmationMailData(String originalSubject, String originalBody) {
        super(originalSubject, originalBody);
    }

    public ConfirmationMailData(Long ticketId, String stateName, boolean accepted) {
        this.transactionId = ticketId;
        this.stateName = stateName;
        this.accepted = accepted;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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
