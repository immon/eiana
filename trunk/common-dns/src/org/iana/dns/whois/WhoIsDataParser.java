package org.iana.dns.whois;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

class WhoIsDataParser {

    public static List<WhoIsData> parse(BufferedReader br) throws IOException {
        String dataLine;
        List<WhoIsData> ret = new ArrayList<WhoIsData>();
        WhoIsData whoIsData = new WhoIsData();

        while ((dataLine = br.readLine()) != null) {
            if (!dataLine.startsWith("%")) {
                if (dataLine.equals("")) {
                    if ((whoIsData.getOrigin() != null) && (!whoIsData.getSeenAt().isEmpty()))
                        ret.add(whoIsData);
                    whoIsData = new WhoIsData();
                } else {
                    SplittedLine sl = new SplittedLine(dataLine, ": ");
                    if (sl.getField().equals("route")) whoIsData.setRoute(sl.getValue());
                    if (sl.getField().equals("origin")) whoIsData.setOrigin(sl.getValue());
                    if (sl.getField().equals("descr")) whoIsData.setDescr(sl.getValue());
                    if (sl.getField().equals("lastupd-frst")) whoIsData.setUpdateFirst(sl.getValue());
                    if (sl.getField().equals("lastupd-last")) whoIsData.setUpdateLast(sl.getValue());
                    if (sl.getField().equals("seen-at")) whoIsData.setSeenAt(sl.getValue(), ",");
                    if (sl.getField().equals("num-rispeers")) whoIsData.setNumRisPeers(sl.getValue());
                    if (sl.getField().equals("source")) whoIsData.setSource(sl.getValue());
                }
            }
        }

        return ret;
    }
}
