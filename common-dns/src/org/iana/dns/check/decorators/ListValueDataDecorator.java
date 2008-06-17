package org.iana.dns.check.decorators;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class ListValueDataDecorator {

    List<ValueDataDecorator> values;

    public ListValueDataDecorator(List<ValueDataDecorator> values) {
        this.values = values;
    }

    public List<ValueDataDecorator> getValues() {
        return values;
    }
}
