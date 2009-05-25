package org.iana.rzm.facade.admin.config;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminEmailTemplateManager {

    void create(EmailTemplateVO template, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    void update(EmailTemplateVO template, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    void delete(String templateName, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    List<EmailTemplateVO> getEmailTemplates() throws InfrastructureException;
}
