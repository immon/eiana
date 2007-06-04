package org.iana.dns.whois;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

public class WhoIsDataRetriever {

    private int DEF_PORT;
    private int TIMEOUT;
    private String WHO_IS;


    public WhoIsDataRetriever() {
        this.DEF_PORT = 43;
        this.TIMEOUT = 3500;
        this.WHO_IS = "riswhois.ripe.net";
    }

    public WhoIsDataRetriever(int DEF_PORT, int TIMEOUT, String WHO_IS) {
        this.DEF_PORT = DEF_PORT;
        this.TIMEOUT = TIMEOUT;
        this.WHO_IS = WHO_IS;
    }

    private List<WhoIsData> retrieve(String ipAddress) {
        Socket whoisSocket;
        InputStreamReader whoisReader;
        BufferedReader fromWhois;
        PrintStream toWhois;
        List<WhoIsData> retData = new ArrayList<WhoIsData>();
        try {
            InetSocketAddress address = new InetSocketAddress(WHO_IS, DEF_PORT);
            whoisSocket = new Socket();
            whoisSocket.connect(address, TIMEOUT);
            whoisSocket.setSoTimeout(TIMEOUT);
            whoisReader = new InputStreamReader(whoisSocket.getInputStream());
            fromWhois = new BufferedReader(whoisReader);
            toWhois = new PrintStream(whoisSocket.getOutputStream());

            toWhois.println(ipAddress);
            retData = WhoIsDataParser.parse(fromWhois);

        } catch (IOException e) {
            Logger.getLogger(WhoIsDataRetriever.class).error("io exception for IPAddress: " + ipAddress, e);
        }
        return retData;
    }

    public String retrieveASNumber(String IPAddress) {
        String bestASNumber = "";
        long seenAtNumber = 0;

        for (WhoIsData data : retrieve(IPAddress))
            if (data.getSeenAt().size() > seenAtNumber) bestASNumber = data.getASNumber();

        return bestASNumber;
    }

}
