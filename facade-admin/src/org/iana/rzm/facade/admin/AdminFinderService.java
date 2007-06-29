package org.iana.rzm.facade.admin;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.auth.AccessDeniedException;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminFinderService<T> {
    
    public int count(Criterion criteria) throws AccessDeniedException;

    public List<T> find(Order order, int offset, int limit) throws AccessDeniedException;

    public List<T> find(Criterion criteria, int offset, int limit) throws AccessDeniedException;

    public List<T> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException;

    public List<T> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException;
    
}
