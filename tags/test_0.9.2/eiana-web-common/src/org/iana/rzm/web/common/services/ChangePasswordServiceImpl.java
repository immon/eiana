package org.iana.rzm.web.common.services;

import org.apache.log4j.Logger;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.passwd.PasswordChangeException;
import org.iana.rzm.facade.passwd.PasswordChangeService;
import org.iana.rzm.web.common.RzmApplicationException;
import org.iana.web.tapestry.services.ServiceInitializer;


public class ChangePasswordServiceImpl implements ChangePasswordService {

    private static final Logger LOGGER = Logger.getLogger(ChangePasswordServiceImpl.class);

    private PasswordChangeService changePasswordService;

    public ChangePasswordServiceImpl(ServiceInitializer<PasswordChangeService> initializer) {
        changePasswordService = initializer.getBean("remotePasswordChangeService", PasswordChangeService.class);
    }

    public void changePassword(String username, String oldPassword, String newPassword, String confirmedNewPassword)
            throws PasswordChangeException {

        try {
            changePasswordService.changePassword(username, oldPassword, newPassword, confirmedNewPassword);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

}
