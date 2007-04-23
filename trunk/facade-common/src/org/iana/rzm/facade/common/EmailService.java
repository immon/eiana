package org.iana.rzm.facade.common;

import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.common.exceptions.InfrastructureException;

/**
 * It provides a set of method to send emails.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface EmailService {

    public void sendEmail(String addresseeEmail, String subject, String body) throws InfrastructureException;

    public void sendEmail(String addresseeEmail, String addresseeName, String subject, String body) throws InfrastructureException;

    public void sendEmailToUser(UserVO addressee, String subject, String body) throws InfrastructureException, NoObjectFoundException;

    public void sendEmailToUser(String userName, String subject, String body) throws InfrastructureException, NoObjectFoundException;
}
