package org.iana.rzm.facade.admin;

import org.aspectj.lang.ProceedingJoinPoint;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.log.Logger;
import org.iana.rzm.user.UserManager;
import org.springframework.core.Ordered;

/**
 * @author Jakub Laszkiewicz
 */
public class UserLoggerAdvisor implements Ordered {
    private int order;
    private Logger logger;
    private UserManager userManager;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public Object log(ProceedingJoinPoint call) throws Throwable {
        RZMStatefulService service = (RZMStatefulService) call.getThis();
        Object user = call.getArgs()[0];
        TrackedObject oldObject;
        Object result;
        TrackedObject object;
        if (user instanceof UserVO) {
            String userName = ((UserVO) user).getUserName();
            oldObject = userManager.getCloned(userName);
            result = call.proceed();
            object = userManager.getCloned(userName);
        } else if (user instanceof String) {
            String userName = (String) user;
            oldObject = userManager.getCloned(userName);
            result = call.proceed();
            object = userManager.getCloned(userName);
        } else {
            Long userId = (Long) user;
            oldObject = userManager.getCloned(userId);
            result = call.proceed();
            object = userManager.getCloned(userId);
        }
        logger.addLog(service.getAuthenticatedUser().getUserName(), "sessionId", call.getSignature().getName(),
                object, oldObject);
        return result;
    }
}
