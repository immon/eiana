package org.iana.rzm.facade.admin;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.criteria.SortCriterion;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
abstract class AdminFinderServiceBean<T> extends AbstractRZMStatefulService implements AdminFinderService<T> {

    protected AdminFinderServiceBean(UserManager userManager) {
        super(userManager);
    }

    public List<T> find(Criterion criteria, Order order, int offset, int limit) {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit);
    }

    public List<T> find(Criterion criteria, List<Order> order, int offset, int limit) {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit);
    }
}
