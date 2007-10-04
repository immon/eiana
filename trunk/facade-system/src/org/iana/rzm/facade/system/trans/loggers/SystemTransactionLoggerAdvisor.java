package org.iana.rzm.facade.system.trans.loggers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.log.Logger;
import org.iana.rzm.trans.TransactionManager;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class SystemTransactionLoggerAdvisor implements Ordered {
    private int order;
    private TransactionManager transactionManager;
    private Logger logger;

    public SystemTransactionLoggerAdvisor(TransactionManager transactionManager) {
        CheckTool.checkNull(transactionManager, "transaction manager");
        this.transactionManager = transactionManager;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Object logObjectCreation(ProceedingJoinPoint call) throws Throwable {
        RZMStatefulService service = (RZMStatefulService) call.getThis();
        Object result = call.proceed();
        List<TransactionVO> trans = (List<TransactionVO>) result;
        for (TransactionVO tran : trans) {
            long transactionId = tran.getTransactionID();
            TrackedObject trackedObject = transactionManager.getTransaction(transactionId);
            logger.addLog(service.getAuthenticatedUser().getUserName(), "sessionId", call.getSignature().getName(),
                    trackedObject, call.getArgs());
        }
        return result;
    }

    public Object logObjectChange(ProceedingJoinPoint call) throws Throwable {
        RZMStatefulService service = (RZMStatefulService) call.getThis();
        Long transactionId = (Long) call.getArgs()[0];
        TrackedObject oldObject = transactionManager.getTransaction(transactionId);
        Object result = call.proceed();
        TrackedObject object = transactionManager.getTransaction(transactionId);
        logger.addLog(service.getAuthenticatedUser().getUserName(), "sessionId", call.getSignature().getName(),
                object, oldObject);
        return result;
    }
}
