package org.iana.dns.whois;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

class WhoIsData {

    enum Type {
        ROUTEv4, ROUTEv6
    }

    private Type type;
    private String route;
    private String origin;
    private String descr;
    private String updateFirst;
    private String updateLast;
    private List<String> seenAt = new ArrayList<String>();
    private long numRisPeers = -1;
    private String source;

    public Type getType() {
        return type;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route, Type type) {
        this.route = route;
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public String getASNumber() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getUpdateFirst() {
        return updateFirst;
    }

    public void setUpdateFirst(String updateFirst) {
        this.updateFirst = updateFirst;
    }

    public String getUpdateLast() {
        return updateLast;
    }

    public void setUpdateLast(String updateLast) {
        this.updateLast = updateLast;
    }

    public List<String> getSeenAt() {
        return seenAt;
    }

    public int getSeenAtNumber() {
        return seenAt.size();
    }

    public void addSeenAt(String seenAt) {
        this.seenAt.add(seenAt);
    }

    public void setSeenAt(String data, String separator) {
        this.seenAt = Arrays.asList(data.split(separator));
    }

    public long getNumRisPeers() {
        return numRisPeers;
    }

    public void setNumRisPeers(long numRisPeers) {
        this.numRisPeers = numRisPeers;
    }

    public void setNumRisPeers(String numRisPeers) {
        this.numRisPeers = Long.parseLong(numRisPeers);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFilled() {
        if ((route == null) || (route.equals(""))) return false;
        if ((origin == null) || (origin.equals(""))) return false;
        if ((descr == null) || (descr.equals(""))) return false;
        if ((updateFirst == null) || (updateFirst.equals(""))) return false;
        if ((updateLast == null) || (updateLast.equals(""))) return false;
        if ((seenAt == null) || (seenAt.isEmpty())) return false;
        if (numRisPeers == -1) return false;
        if ((source == null) || (source.equals(""))) return false;

        return true;
    }
}
