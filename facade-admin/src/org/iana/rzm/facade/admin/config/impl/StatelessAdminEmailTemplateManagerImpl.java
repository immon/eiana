package org.iana.rzm.facade.admin.config.impl;

import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.def.TemplateDefConfig;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.admin.config.EmailTemplateVO;
import org.iana.rzm.facade.admin.config.StatelessAdminEmailTemplateManager;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.user.UserManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class StatelessAdminEmailTemplateManagerImpl extends AbstractRZMStatelessService implements StatelessAdminEmailTemplateManager {

    private TemplateDefConfig templateDefConfig;

    public StatelessAdminEmailTemplateManagerImpl(UserManager userManager, TemplateDefConfig templateDefConfig) {
        super(userManager);
        CheckTool.checkNull(templateDefConfig, "template def config");
        this.templateDefConfig = templateDefConfig;
    }

    public void create(EmailTemplateVO template, AuthenticatedUser authUser) throws InfrastructureException {
        isRoot(authUser);
        templateDefConfig.create(toTemplateDef(template));
    }

    public void update(EmailTemplateVO template, AuthenticatedUser authUser) throws InfrastructureException {
        isRoot(authUser);
        templateDefConfig.update(toTemplateDef(template));
    }

    public void delete(String templateName, AuthenticatedUser authUser) throws InfrastructureException {
        isRoot(authUser);
        templateDefConfig.delete(templateName);
    }

    public List<EmailTemplateVO> getEmailTemplates() throws InfrastructureException {
        return toTemplateVOs(templateDefConfig.getTemplateDefs());
    }

    private TemplateDef toTemplateDef(EmailTemplateVO src) {
        TemplateDef ret = new TemplateDef();
        ret.setType(src.getName());
        ret.setMailSenderType(src.getMailSenderType());
        ret.setSubject(src.getSubject());
        ret.setContent(src.getContent());
        ret.setSigned(src.isSigned());
        ret.setAddressees(src.getAddressees());
        return ret;
    }

    private List<EmailTemplateVO> toTemplateVOs(List<TemplateDef> src) {
        List<EmailTemplateVO> ret = new ArrayList<EmailTemplateVO>();
        if (src != null) {
            for (TemplateDef tmp : src) {
                ret.add(toTemplateVO(tmp));
            }
        }
        return ret;
    }

    private EmailTemplateVO toTemplateVO(TemplateDef src) {
        EmailTemplateVO ret = new EmailTemplateVO();
        ret.setName(src.getType());
        ret.setMailSenderType(src.getMailSenderType());
        ret.setSubject(src.getSubject());
        ret.setContent(src.getContent());
        ret.setSigned(src.isSigned());

        Set<String>addresses = new HashSet<String>();

        for (String s : src.getAddressees()) {
            addresses.add(s);
        }

        ret.setAddressees(addresses);
        return ret;
    }
}
