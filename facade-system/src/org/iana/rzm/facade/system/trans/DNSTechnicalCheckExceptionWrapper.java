package org.iana.rzm.facade.system.trans;

import org.iana.dns.check.DNSExceptionMessagesVisitor;
import org.iana.dns.check.DNSTechnicalCheckException;


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
