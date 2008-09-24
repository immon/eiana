package org.iana.rzm.facade.services;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.criteria.SortCriterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public abstract class AbstractFinderService<T> extends AbstractRZMStatefulService implements FinderService<T> {

    protected AbstractFinderService(UserManager userManager) {
        super(userManager);
    }

    public List<T> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return find(null, order, offset, limit);
    }

    public List<T> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit);
    }

    public List<T> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit);
    }
}
