package org.iana.rzm.web.common.query;

import org.iana.rzm.web.common.model.criteria.*;

public interface  Sortable {
    public void setSortOrder(SortOrder sortOrder);
    public SortOrder getSortOrder();
}
