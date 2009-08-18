package org.iana.notifications.template.simple;

import org.iana.notifications.PContent;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateInstantiationException;
import org.iana.notifications.template.def.TemplateDef;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class SimpleTemplate implements Template {

    private String subjectTemplate;

    private String bodyTemplate;

    private String mailSenderType;

    private StringTemplateAlgorithm algorithm;

    private AddresseeProducer addresseeProducer;

    public SimpleTemplate(TemplateDef templateDef, StringTemplateAlgorithm algorithm) {
        CheckTool.checkNull(templateDef, "template definition");
        this.subjectTemplate = templateDef.getSubject();
        this.bodyTemplate = templateDef.getContent();
        this.mailSenderType = templateDef.getMailSenderType();
        this.algorithm = algorithm;
    }

    public PContent instantiate(Object data) throws TemplateInstantiationException {
        try {
            String subject = algorithm.instantiateString(subjectTemplate, data);
            String body = algorithm.instantiateString(bodyTemplate, data);
            return new PContent(subject, body);
        } catch (StringTemplateException e) {
            throw new TemplateInstantiationException(e);
        }
    }

    public String getMailSenderType() {
        return mailSenderType;
    }

    public AddresseeProducer getAddresseeProducer() {
        return addresseeProducer;
    }

    public void setAddresseeProducer(AddresseeProducer addresseeProducer) {
        this.addresseeProducer = addresseeProducer;
    }

}
