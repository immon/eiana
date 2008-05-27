package org.iana.rzm.web.model;

import org.iana.rzm.facade.admin.msgs.*;
import org.iana.rzm.web.util.*;

import java.sql.*;


public class PollMessageVOWrapper extends ValueObject implements PaginatedEntity {
    private long id;
    private Long rtId;
    private String message;
    private String messageId;
    private long transactionId;
    private Timestamp created;
    private String domainName;


    public PollMessageVOWrapper(long id, long transactionId, Long rtId, String message, String messageId, Timestamp created, String domainName) {
        this.id = id;
        this.rtId = rtId;
        this.message = message;
        this.messageId = messageId;
        this.transactionId = transactionId;
        this.created = created;
        this.domainName = domainName;
    }

    public PollMessageVOWrapper(PollMsgVO vo){
        this(vo.getId(), vo.getTransactionID(), vo.getTicketID(),vo.getStatus(), vo.getEppID(), vo.getCreated(), vo.getDomainName());
    }

    public long getId(){
        return id;
    }

    public long getRtId(){
        return rtId;
    }

    public String getMessageId(){
        return messageId;
    }

    public String getMessage(){
        return message;
    }

    public String getCreated() {
        return DateUtil.formatDate(created);
    }

    public long getTransactionId() {
        return transactionId;
    }

    public String getDomainName() {
        return domainName;
    }
}
