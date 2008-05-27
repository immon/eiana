package org.iana.rzm.web.common;

import org.iana.rzm.web.model.criteria.*;

public interface Sortable {
    public void setSortOrder(SortOrder sortOrder);
    public SortOrder getSortOrder();
}
