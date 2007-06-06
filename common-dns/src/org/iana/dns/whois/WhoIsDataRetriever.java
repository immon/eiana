package org.iana.dns.whois;

import org.apache.log4j.Logger;
import org.iana.dns.DNSWhoIsData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

public class WhoIsDataRetriever implements DNSWhoIsData {

    private int defaultPort;
    private int timeout;
    private String whoIsServer;

    public WhoIsDataRetriever() {
        this.defaultPort = 43;
        this.timeout = 3500;
        this.whoIsServer = "riswhois.ripe.net";
    }

    public WhoIsDataRetriever(int defaultPort, int timeout, String whoIsServer) {
        this.defaultPort = defaultPort;
        this.timeout = timeout;
        this.whoIsServer = whoIsServer;
    }

    private List<WhoIsData> retrieve(String ipAddress) throws IOException {
        Socket whoisSocket;
        InputStreamReader whoisReader;
        BufferedReader fromWhois;
        PrintStream toWhois;
        List<WhoIsData> retData;
        try {
            InetSocketAddress address = new InetSocketAddress(whoIsServer, defaultPort);
            whoisSocket = new Socket();
            whoisSocket.connect(address, timeout);
            whoisSocket.setSoTimeout(timeout);
            whoisReader = new InputStreamReader(whoisSocket.getInputStream());
            fromWhois = new BufferedReader(whoisReader);
            toWhois = new PrintStream(whoisSocket.getOutputStream());

            toWhois.println(ipAddress);
            retData = WhoIsDataParser.parse(fromWhois);

        } catch (IOException e) {
            Logger.getLogger(WhoIsDataRetriever.class).error("io exception for IPAddress: " + ipAddress, e);
            throw e;
        }
        return retData;
    }

    public String retrieveASNumber(String IPAddress) throws IOException {
        String bestASNumber = "";
        long seenAtNumber = 0;

        for (WhoIsData data : retrieve(IPAddress))
            if (data.getSeenAtNumber() > seenAtNumber) {
                bestASNumber = data.getASNumber();
                seenAtNumber = data.getSeenAtNumber();
            }

        return bestASNumber;
    }

}
