package org.iana.rzm.web.tapestry.components.list;

import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;

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
