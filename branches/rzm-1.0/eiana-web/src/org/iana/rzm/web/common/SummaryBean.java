package org.iana.rzm.web.common;

import org.iana.rzm.web.model.*;

import java.io.*;
import java.util.*;

public class SummaryBean implements Serializable {

    private long rtId;
    private List<ActionVOWrapper> changes;

    public SummaryBean(long rtId, List<ActionVOWrapper> changes) {
        this.rtId = rtId;
        this.changes = changes;
    }

    public List<ActionVOWrapper> getChanges() {
        return changes;
    }

    public String getTicketNumber() {
        if (rtId == 0) {
            return "Ticket number is not assign yet";
        }
        return "Ticket " + rtId;
    }
}
