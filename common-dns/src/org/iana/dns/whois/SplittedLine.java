package org.iana.dns.whois;

/**
 * @author: Piotr Tkaczyk
 */

class SplittedLine {

    private String splitter;
    private String field;
    private String value;

    public SplittedLine(String data, String splitter) {
        this.splitter = splitter;
        split(data);
    }

    public SplittedLine(String data) {
        splitter = ": ";
        split(data);
    }

    private void split(String data) {
        int splitterPos = data.indexOf(splitter);
        if ((splitterPos > 1) && (splitterPos < data.length())) {
            field = data.substring(0, splitterPos).trim();
            value = data.substring(splitterPos + 1, data.length()).trim();
        }
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
