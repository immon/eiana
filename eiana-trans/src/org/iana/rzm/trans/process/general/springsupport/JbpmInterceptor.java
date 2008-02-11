package org.iana.rzm.trans.process.general.springsupport;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.iana.rzm.trans.dao.ProcessDAO;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmInterceptor implements MethodInterceptor {
    private ProcessDAO processDAO;


    public void setProcessDAO(ProcessDAO processDAO) {
        this.processDAO = processDAO;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } finally {
            processDAO.close();
        }
    }
}
