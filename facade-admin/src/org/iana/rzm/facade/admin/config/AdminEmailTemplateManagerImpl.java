package org.iana.rzm.facade.admin.config;

import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.def.TemplateDefConfig;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AdminEmailTemplateManagerImpl implements AdminEmailTemplateManager {

    private TemplateDefConfig templateDefConfig;

    public AdminEmailTemplateManagerImpl(TemplateDefConfig templateDefConfig) {
        CheckTool.checkNull(templateDefConfig, "template def config");
        this.templateDefConfig = templateDefConfig;
    }

    public void create(EmailTemplateVO template) throws InfrastructureException {
        templateDefConfig.create(toTemplateDef(template));
    }

    public void update(EmailTemplateVO template) throws InfrastructureException {
        templateDefConfig.update(toTemplateDef(template));
    }

    public void delete(String templateName) throws InfrastructureException {
        templateDefConfig.delete(templateName);
    }

    public List<EmailTemplateVO> getEmailTemplates() throws InfrastructureException {
        return toTemplateVOs(templateDefConfig.getTemplateDefs());
    }

    private TemplateDef toTemplateDef(EmailTemplateVO src) {
        TemplateDef ret = new TemplateDef();
        ret.setType(src.getName());
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
        ret.setSubject(src.getSubject());
        ret.setContent(src.getContent());
        ret.setSigned(src.isSigned());
        ret.setAddressees(src.getAddressees());
        return ret;
    }

}
