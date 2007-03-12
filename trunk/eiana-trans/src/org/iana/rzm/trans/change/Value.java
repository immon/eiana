package org.iana.rzm.trans.change;

public interface Value<T extends Change> {

    public void accept(ValueVisitor value);
}
