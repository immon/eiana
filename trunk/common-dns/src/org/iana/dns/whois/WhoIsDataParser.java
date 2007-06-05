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
                    if (whoIsData.isFilled())
                        ret.add(whoIsData);
                    whoIsData = new WhoIsData();
                } else {
                    SplittedLine sl = new SplittedLine(dataLine, ": ");
                    if ("route".equals(sl.getField())) whoIsData.setRoute(sl.getValue());
                    if ("route6".equals(sl.getField())) whoIsData.setRoute(sl.getValue());
                    if ("origin".equals(sl.getField())) whoIsData.setOrigin(sl.getValue());
                    if ("descr".equals(sl.getField())) whoIsData.setDescr(sl.getValue());
                    if ("lastupd-frst".equals(sl.getField())) whoIsData.setUpdateFirst(sl.getValue());
                    if ("lastupd-last".equals(sl.getField())) whoIsData.setUpdateLast(sl.getValue());
                    if ("seen-at".equals(sl.getField())) whoIsData.setSeenAt(sl.getValue(), ",");
                    if ("num-rispeers".equals(sl.getField())) whoIsData.setNumRisPeers(sl.getValue());
                    if ("source".equals(sl.getField())) whoIsData.setSource(sl.getValue());
                }
            }
        }

        return ret;
    }
}
