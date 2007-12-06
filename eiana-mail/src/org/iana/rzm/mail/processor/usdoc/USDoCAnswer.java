package org.iana.rzm.mail.processor.usdoc;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class USDoCAnswer implements MessageData {

    private long ticketID;

    private String eppID;

    private String changeSummary;

    private boolean accept;

    private boolean nameserverChange;

    private boolean pgp;

    public USDoCAnswer(long ticketID, String eppID, String changeSummary, boolean accept, boolean nameserverChange) {
        this.ticketID = ticketID;
        this.eppID = eppID;
        this.changeSummary = changeSummary;
        this.accept = accept;
        this.nameserverChange = nameserverChange;
    }

    public long getTicketID() {
        return ticketID;
    }

    public String getEppID() {
        return eppID;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean isNameserverChange() {
        return nameserverChange;
    }

    public boolean isPgp() {
        return pgp;
    }

    public void setPgp(boolean pgp) {
        this.pgp = pgp;
    }
}
