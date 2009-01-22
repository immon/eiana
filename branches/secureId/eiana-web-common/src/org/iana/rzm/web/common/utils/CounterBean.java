package org.iana.rzm.web.common.utils;

import java.io.*;


public class CounterBean implements Serializable {

    private int counter;

    public CounterBean() {
        counter = 1;
    }

    public int getCounter() {
        int current = counter;
        counter++;
        return current;
    }
}