package org.iana.rzm.facade.admin;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.log.Logger;
import org.iana.rzm.user.UserManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class UserLoggerInterceptor implements MethodInterceptor {
    private static final String loggedMethodsArray[] = {"createUser", "updateUser", "deleteUser"};
    private static final List<String> loggedMethods = Arrays.asList(loggedMethodsArray);

    private Logger logger;
    private UserManager userManager;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        RZMStatefulService service = (RZMStatefulService) methodInvocation.getThis();
        if (loggedMethods.contains(methodInvocation.getMethod().getName())) {
            Object user = methodInvocation.getArguments()[0];
            TrackedObject oldObject;
            Object result;
            TrackedObject object;
            if (user instanceof UserVO) {
                String userName = ((UserVO) user).getUserName();
                oldObject = userManager.getCloned(userName);
                result = methodInvocation.proceed();
                object = userManager.getCloned(userName);
            } else if (user instanceof String) {
                String userName = (String) user;
                oldObject = userManager.getCloned(userName);
                result = methodInvocation.proceed();
                object = userManager.getCloned(userName);
            } else {
                Long userId = (Long) user;
                oldObject = userManager.getCloned(userId);
                result = methodInvocation.proceed();
                object = userManager.getCloned(userId);
            }
            logger.addLog(service.getUser().getUserName(), "sessionId", methodInvocation.getMethod().getName(),
                    object, oldObject);
            return result;
        } else return methodInvocation.proceed();
    }
}
