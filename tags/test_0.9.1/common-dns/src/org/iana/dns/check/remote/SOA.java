package org.iana.dns.check.remote;

import org.xbill.DNS.Message;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SOA {
    public String nameServer;
    public byte[] message;
    public boolean udp;

    public SOA() {
    }

    public SOA(String nameServer, Message message, boolean udp) {
        this.nameServer = nameServer;
        this.message = message == null ? null : message.toWire();
        this.udp = udp;
    }
}
