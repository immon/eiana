package org.iana.rzm.web.common;

public interface SortFactory {
    public boolean isFieldSortable(String name);
    public void sort(String field, boolean accending);
}
