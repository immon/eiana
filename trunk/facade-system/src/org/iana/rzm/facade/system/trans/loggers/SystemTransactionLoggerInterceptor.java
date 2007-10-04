package org.iana.rzm.facade.system.trans.loggers;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.log.Logger;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class SystemTransactionLoggerInterceptor implements MethodInterceptor {
    private static final String creationMethodsArray[] = {"createTransactions"};
    private static final String changeMethodsArray[] = {
            "acceptTransaction", "rejectTransaction", "transitTransaction"};
    private static final List<String> creationMethods = Arrays.asList(creationMethodsArray);
    private static final List<String> changeMethods = Arrays.asList(changeMethodsArray);

    private static final String LOGGING_USER_MAIL_NAME = "MAIL";

    private TransactionManager transactionManager;
    private Logger logger;

    public SystemTransactionLoggerInterceptor(TransactionManager transactionManager) {
        CheckTool.checkNull(transactionManager, "transaction manager");
        this.transactionManager = transactionManager;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        TransactionService service = (TransactionService) methodInvocation.getThis();
        if (creationMethods.contains(methodInvocation.getMethod().getName()))
            return logObjectCreation(methodInvocation, service, "sessionId");
        else if (changeMethods.contains(methodInvocation.getMethod().getName()))
            return logObjectChange(methodInvocation, service, "sessionId");
        else
            return methodInvocation.proceed();
    }

    private Object logObjectCreation(MethodInvocation methodInvocation, TransactionService service,
                                     String sessionId) throws Throwable {
        Object result = methodInvocation.proceed();
        List<TransactionVO> trans = (List<TransactionVO>) result;
        for (TransactionVO tran : trans) {
            long transactionId = tran.getTransactionID();
            TrackedObject trackedObject = transactionManager.getTransaction(transactionId);
            logger.addLog(LOGGING_USER_MAIL_NAME, sessionId, methodInvocation.getMethod().getName(),
                    trackedObject, methodInvocation.getArguments());
        }
        return result;
    }

    private Object logObjectChange(MethodInvocation methodInvocation, TransactionService service,
                                   String sessionId) throws Throwable {
        Long transactionId = (Long) methodInvocation.getArguments()[0];
        TrackedObject oldObject = transactionManager.getTransaction(transactionId);
        Object result = methodInvocation.proceed();
        TrackedObject object = transactionManager.getTransaction(transactionId);
        logger.addLog(LOGGING_USER_MAIL_NAME, sessionId, methodInvocation.getMethod().getName(),
                object, oldObject);
        return result;
    }
}
