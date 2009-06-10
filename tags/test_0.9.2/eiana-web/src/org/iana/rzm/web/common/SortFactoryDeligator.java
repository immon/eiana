package org.iana.rzm.web.common;

import org.iana.rzm.web.model.criteria.*;

public class SortFactoryDeligator implements SortFactory {
    private SortFactory factory;
    private Sortable sortable;

    public SortFactoryDeligator(SortFactory factory, Sortable sortable) {
        this.factory = factory;
        this.sortable = sortable;
    }

    public boolean isFieldSortable(String name) {
        return factory.isFieldSortable(name);
    }

    public void sort(String field, boolean accending) {
        sortable.setSortOrder(new SortOrder(field, accending));
        factory.sort(field, accending);
    }
}
