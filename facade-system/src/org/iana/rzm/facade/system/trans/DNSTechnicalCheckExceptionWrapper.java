package org.iana.rzm.facade.system.trans;

import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.check.DNSExceptionMessagesVisitor;


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
