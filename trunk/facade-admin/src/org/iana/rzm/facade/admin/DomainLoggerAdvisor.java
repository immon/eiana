package org.iana.rzm.facade.admin;

import org.aopalliance.intercept.MethodInvocation;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.log.Logger;
import org.springframework.core.Ordered;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author Jakub Laszkiewicz
 */
public class DomainLoggerAdvisor implements Ordered {
    private int order;
    private Logger logger;
    private DomainManager domainManager;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setDomainManager(DomainManager domainManager) {
        this.domainManager = domainManager;
    }

    public Object log(ProceedingJoinPoint call) throws Throwable {
        RZMStatefulService service = (RZMStatefulService) call.getThis();
        Object domain = call.getArgs()[0];
        TrackedObject oldObject;
        Object result;
        TrackedObject object;
        if (domain instanceof DomainVO) {
            String domainName = ((DomainVO) domain).getName();
            oldObject = domainManager.getCloned(domainName);
            result = call.proceed();
            object = domainManager.getCloned(domainName);
        } else if (domain instanceof String) {
            String domainName = (String) domain;
            oldObject = domainManager.getCloned(domainName);
            result = call.proceed();
            object = domainManager.getCloned(domainName);
        } else {
            Long domainId = (Long) domain;
            oldObject = domainManager.getCloned(domainId);
            result = call.proceed();
            object = domainManager.getCloned(domainId);
        }
        logger.addLog(service.getAuthenticatedUser().getUserName(), "sessionId", call.getSignature().getName(),
                object, oldObject);
        return result;
    }
}
