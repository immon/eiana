package org.iana.rzm.facade.admin.config.impl;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.admin.config.AdminEmailTemplateManager;
import org.iana.rzm.facade.admin.config.EmailTemplateVO;
import org.iana.rzm.facade.admin.config.StatelessAdminEmailTemplateManager;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.user.UserVOManager;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AdminEmailTemplateManagerImpl extends AbstractRZMStatefulService implements AdminEmailTemplateManager {

    StatelessAdminEmailTemplateManager statelessAdminEmailTemplateManager;

    public AdminEmailTemplateManagerImpl(UserVOManager userManager, StatelessAdminEmailTemplateManager statelessAdminEmailTemplateManager) {
        super(userManager);
        CheckTool.checkNull(statelessAdminEmailTemplateManager, "admin email template manager");
        this.statelessAdminEmailTemplateManager = statelessAdminEmailTemplateManager;
    }

    public void create(EmailTemplateVO template) throws InfrastructureException {
        statelessAdminEmailTemplateManager.create(template, getAuthenticatedUser());
    }

    public void update(EmailTemplateVO template) throws InfrastructureException {
        statelessAdminEmailTemplateManager.update(template, getAuthenticatedUser());
    }

    public void delete(String templateName) throws InfrastructureException {
        statelessAdminEmailTemplateManager.delete(templateName, getAuthenticatedUser());
    }

    public List<EmailTemplateVO> getEmailTemplates() throws InfrastructureException {
        return statelessAdminEmailTemplateManager.getEmailTemplates();
    }

}
