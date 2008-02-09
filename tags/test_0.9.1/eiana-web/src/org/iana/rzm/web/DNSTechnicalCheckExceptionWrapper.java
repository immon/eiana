package org.iana.rzm.web;

import org.iana.dns.check.*;

public class DNSTechnicalCheckExceptionWrapper extends Exception {

    private String message;

    public DNSTechnicalCheckExceptionWrapper(DNSTechnicalCheckException e) {
        DNSExceptionMessagesVisitor messagesVisitor = new DNSExceptionMessagesVisitor();
        e.accept(messagesVisitor);
        message = messagesVisitor.getMessages();
    }

    public String getMessage(){
        return message;
    }
    
}
