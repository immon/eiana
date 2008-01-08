package org.iana.criteria;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SortCriterion implements Criterion {

    private Criterion criterion;
    private List<Order> orders = new ArrayList<Order>();

    public SortCriterion(Criterion criterion, Order order) {
        this(criterion, Arrays.asList(order));
    }

    public SortCriterion(Criterion criterion, List<Order> orders) {
        if (orders == null || orders.isEmpty()) throw new IllegalArgumentException("null or empty orders");
        for (Order order : orders) {
            if (order == null) throw new IllegalArgumentException("null order on orders list");
        }
        this.criterion = criterion;
        this.orders = orders;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitSort(this);
    }
}
