package org.iana.rzm.web.admin.model;

import org.iana.rzm.facade.admin.config.EmailTemplateVO;
import org.iana.rzm.web.common.model.ValueObject;

import java.util.Set;


public class EmailTemplateVOWrapper extends ValueObject {
    private EmailTemplateVO template;

    public EmailTemplateVOWrapper(EmailTemplateVO template){
        this.template = template;
        if(this.template == null){
            this.template = new EmailTemplateVO();
        }
    }

    public String getName() {
        return template.getName();
    }

    public void setName(String name) {
        template.setName(name);
    }

    public String getSubject() {
        return template.getSubject();
    }

    public void setSubject(String subject) {
        template.setSubject(subject);
    }

    public String getContent() {
        return template.getContent();
    }

    public void setContent(String content) {
        template.setContent(content);
    }

    public boolean isSigned() {
        return template.isSigned();
    }

    public void setSigned(boolean signed) {
        template.setSigned(signed);
    }

    public Set<String> getAddressees() {
        return template.getAddressees();
    }

    public void setAddressees(Set<String> addressees) {
        template.setAddressees(addressees);
    }

    public EmailTemplateVO getConfig() {
        return template;
    }
}
