package org.iana.dns.check.remote.client;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSNameServer;
import org.xbill.DNS.Message;

import javax.xml.rpc.Stub;
import java.util.*;
import java.rmi.RemoteException;
import java.io.IOException;

/**
 * @author Jakub Laszkiewicz
 */
public class TechnicalCheckClient {
    private RPCTechnicalCheck technicalCheck;

    public TechnicalCheckClient(String address) {
        technicalCheck = new TechnicalCheck_Impl().getRPCTechnicalCheckPort();
        ((Stub) technicalCheck)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, address);
    }

    public Set<DNSNameServer> getSOA(DNSDomain domain) throws TechnicalCheckClientException {
        try {
            String[] ns = new String[domain.getNameServers().size()];
            StringArray[] ip = new StringArray[domain.getNameServers().size()];
            int i = 0;
            for (DNSHost host : domain.getNameServers()) {
                ns[i] = host.getName();
                ip[i] = new StringArray(host.getIPAddressesAsStrings().toArray(new String[0]));
                i++;
            }

            SOA soas[] = technicalCheck.querySOA(domain.getName(), ns, ip);

            Map<String, Message> tcpSoas = new HashMap<String, Message>();
            Map<String, Message> udpSoas = new HashMap<String, Message>();
            for (SOA soa : soas) {
                if (soa.isUdp())
                    udpSoas.put(soa.getNameServer(), new Message(soa.getMessage()));
                else
                    tcpSoas.put(soa.getNameServer(), new Message(soa.getMessage()));
            }

            Set<DNSNameServer> result = new HashSet<DNSNameServer>();
            for (DNSHost host : domain.getNameServers()) {
                result.add(new DNSNameServer(domain, host, udpSoas.get(host.getName()),
                        tcpSoas.get(host.getName())));
            }
            return result;
        } catch (RPCTechnicalCheckException e) {
            throw new TechnicalCheckClientException(e);
        } catch (RemoteException e) {
            throw new TechnicalCheckClientException(e);
        } catch (IOException e) {
            throw new TechnicalCheckClientException(e);
        }
    }
}
