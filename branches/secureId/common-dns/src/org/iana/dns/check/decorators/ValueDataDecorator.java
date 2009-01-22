package org.iana.dns.check.decorators;

/**
 * @author Piotr Tkaczyk
 */
public class ValueDataDecorator {

    private String name;
    private String value;

    public ValueDataDecorator(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ValueDataDecorator(String name, int value) {
        this(name, "" + value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
