package org.iana.rzm.web.tapestry.components.list;

public interface SortFactory {
    public boolean isFieldSortable(String name);
    public void sort(String field, boolean accending);
}
