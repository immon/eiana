package org.iana.dns.check.decorators;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public class ExceptionDataDecorator {

    private String name;
    private String hostName;
    private List<ValueDataDecorator> received;
    private List<ValueDataDecorator> expected;
    private List<ValueDataDecorator> other;

    public ExceptionDataDecorator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHost() {
        return hostName;
    }

    public void setReceived(List<ValueDataDecorator> received) {
        this.received = received;
    }

    public void addReceived(ValueDataDecorator value) {
        if (this.received == null)
            this.received = new ArrayList<ValueDataDecorator>();

        this.received.add(value);
    }

    public void setExpected(List<ValueDataDecorator> expected) {
        this.expected = expected;
    }

    public void addExpected(ValueDataDecorator value) {
        if (this.expected == null)
            this.expected = new ArrayList<ValueDataDecorator>();

        this.expected.add(value);
    }

    public void setOther(List<ValueDataDecorator> other) {
        this.other = other;
    }

    public void addOther(ValueDataDecorator value) {
        if (this.other == null)
            this.other = new ArrayList<ValueDataDecorator>();

        this.other.add(value);
    }

    public ListValueDataDecorator getReceived() {
        return (received == null || received.isEmpty())? null : new ListValueDataDecorator(received);
    }

    public ListValueDataDecorator getExpected() {
        return (expected == null || expected.isEmpty())? null : new ListValueDataDecorator(expected);
    }

    public ListValueDataDecorator getOther() {
        return (other == null || other.isEmpty())? null : new ListValueDataDecorator(other);
    }
}
