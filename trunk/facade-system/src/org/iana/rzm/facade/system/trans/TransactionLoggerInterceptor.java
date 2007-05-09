package org.iana.rzm.facade.system.trans;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.log.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionLoggerInterceptor implements MethodInterceptor {
    private static final String creationMethodsArray[] = {"createTransaction"};
    private static final String changeMethodsArray[] = {
            "acceptTransaction", "rejectTransaction", "transitTransaction"};
    private static final List<String> creationMethods = Arrays.asList(creationMethodsArray);
    private static final List<String> changeMethods = Arrays.asList(changeMethodsArray);

    private Logger logger;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        SystemTransactionService service = (SystemTransactionService) methodInvocation.getThis();
        if (creationMethods.contains(methodInvocation.getMethod().getName()))
            return logObjectCreation(methodInvocation, service, "sessionId");
        else if (changeMethods.contains(methodInvocation.getMethod().getName()))
            return logObjectChange(methodInvocation, service, "sessionId");
        else
            return methodInvocation.proceed();
    }

    private Object logObjectCreation(MethodInvocation methodInvocation, SystemTransactionService service,
                                     String sessionId) throws Throwable {
        Object result = methodInvocation.proceed();
        Long transactionId = ((TransactionVO) result).getTransactionID();
        TrackedObject trackedObject = service.getTransaction(transactionId);
        logger.addLog(service.getUser().getUserName(), sessionId, methodInvocation.getMethod().getName(),
                trackedObject, methodInvocation.getArguments());
        return result;
    }

    private Object logObjectChange(MethodInvocation methodInvocation, SystemTransactionService service,
                                   String sessionId) throws Throwable {
        Long transactionId = (Long) methodInvocation.getArguments()[0];
        TrackedObject oldObject = service.getTransaction(transactionId);
        Object result = methodInvocation.proceed();
        TrackedObject object = service.getTransaction(transactionId);
        logger.addLog(service.getUser().getUserName(), sessionId, methodInvocation.getMethod().getName(),
                object, oldObject);
        return result;
    }
}
