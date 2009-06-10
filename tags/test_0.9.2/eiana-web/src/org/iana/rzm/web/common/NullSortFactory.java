package org.iana.rzm.web.common;

public class NullSortFactory implements SortFactory {
    public boolean isFieldSortable(String name) {
        return false;
    }

    public void sort(String field, boolean accending) {

    }
}
