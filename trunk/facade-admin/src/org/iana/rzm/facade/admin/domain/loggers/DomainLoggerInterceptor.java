package org.iana.rzm.facade.admin.domain.loggers;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.log.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class DomainLoggerInterceptor implements MethodInterceptor {
    private static final String loggedMethodsArray[] = {"createDomain", "updateDomain", "deleteDomain"};
    private static final List<String> loggedMethods = Arrays.asList(loggedMethodsArray);

    private Logger logger;
    private DomainManager domainManager;


    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setDomainManager(DomainManager domainManager) {
        this.domainManager = domainManager;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        RZMStatefulService service = (RZMStatefulService) methodInvocation.getThis();
        if (loggedMethods.contains(methodInvocation.getMethod().getName())) {
            Object domain = methodInvocation.getArguments()[0];
            TrackedObject oldObject;
            Object result;
            TrackedObject object;
            if (domain instanceof DomainVO) {
                String domainName = ((DomainVO) domain).getName();
                oldObject = domainManager.getCloned(domainName);
                result = methodInvocation.proceed();
                object = domainManager.getCloned(domainName);
            } else if (domain instanceof String) {
                String domainName = (String) domain;
                oldObject = domainManager.getCloned(domainName);
                result = methodInvocation.proceed();
                object = domainManager.getCloned(domainName);
            } else {
                Long domainId = (Long) domain;
                oldObject = domainManager.getCloned(domainId);
                result = methodInvocation.proceed();
                object = domainManager.getCloned(domainId);
            }
            logger.addLog(service.getAuthenticatedUser().getUserName(), "sessionId", methodInvocation.getMethod().getName(),
                    object, oldObject);
            return result;
        } else return methodInvocation.proceed();
    }
}
