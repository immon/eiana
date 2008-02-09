package org.iana.rzm.web.services;

public interface EmailService {

    void sendLogMessage(String msg, Throwable value);
}
