package org.iana.rzm.facade.admin;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.rzm.facade.system.domain.IDomainVO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminFinderService<T> {
    
    public int count(Criterion criteria);

    public List<T> find(Criterion criteria, int offset, int limit);

    public List<T> find(Criterion criteria, Order order, int offset, int limit);

    public List<T> find(Criterion criteria, List<Order> order, int offset, int limit);
    
}
