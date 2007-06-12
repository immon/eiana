package org.iana.rzm.trans.jbpm;

import org.iana.rzm.trans.dao.ProcessDAO;
import org.springframework.core.Ordered;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmAdvisor implements Ordered {
    private int order;
    private ProcessDAO processDAO;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setProcessDAO(ProcessDAO processDAO) {
        this.processDAO = processDAO;
    }

    public void close() throws Throwable {
        System.out.println("*** jbpm advisor - close");
        processDAO.close();
    }
}
