package org.iana.rzm.facade.services;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.common.exceptions.InfrastructureException;

import java.util.List;

/**
 * The finder service based on criteria.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface FinderService<T> {

    public T get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException;

    public int count(Criterion criteria) throws AccessDeniedException, InfrastructureException;

    public List<T> find(Criterion criteria) throws AccessDeniedException, InfrastructureException;

    public List<T> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException;

    public List<T> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException;

    public List<T> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException;

    public List<T> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException, InfrastructureException;
        
}
