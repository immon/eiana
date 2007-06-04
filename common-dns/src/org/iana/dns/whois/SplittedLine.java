package org.iana.dns.whois;

/**
 * @author: Piotr Tkaczyk
 */

class SplittedLine {
    private String field;
    private String value;

    public SplittedLine(String data, String splitter) {
        String[] splited = data.split(splitter);
        field = splited[0].trim();
        value = splited[1].trim();
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
