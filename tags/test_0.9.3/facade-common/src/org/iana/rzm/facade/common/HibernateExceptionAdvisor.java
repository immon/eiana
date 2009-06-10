package org.iana.rzm.facade.common;

import org.springframework.core.Ordered;
import org.aspectj.lang.ProceedingJoinPoint;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.hibernate.HibernateException;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateExceptionAdvisor implements Ordered {
    private int order;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object proceed(ProceedingJoinPoint call) throws Throwable {
        try {
            return call.proceed();
        } catch (HibernateException e) {
            throw new InfrastructureException(e);
        }
    }
}
