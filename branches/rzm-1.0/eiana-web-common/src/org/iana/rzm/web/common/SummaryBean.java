package org.iana.rzm.web.common;

import org.iana.rzm.web.common.model.*;

import java.io.*;
import java.util.*;

public class SummaryBean implements Serializable {

    private long rtId;
    private List<ActionVOWrapper> changes;
    private MessageUtil messageUtil;

    public SummaryBean(long rtId, List<ActionVOWrapper> changes, MessageUtil messageUtil) {
        this.rtId = rtId;
        this.changes = changes;
        this.messageUtil = messageUtil;
    }

    public List<ActionVOWrapper> getChanges() {
        return changes;
    }

    public String getTicketNumber() {
        if (rtId == 0) {
            return messageUtil.getTicketNotAssignMessage();
        }
        return "Ticket " + rtId;
    }
}
