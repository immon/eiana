package org.iana.rzm.web.services;

public class SequenceGeneratorImpl implements SequenceGenerator {
    private long sequence;

    public SequenceGeneratorImpl(long sequence) {
        this.sequence = sequence;
    }

    public SequenceGeneratorImpl() {
        this((long) (Math.random() * Long.MAX_VALUE / 2l));
    }

    public long next() {
        return ++sequence;
    }
}
