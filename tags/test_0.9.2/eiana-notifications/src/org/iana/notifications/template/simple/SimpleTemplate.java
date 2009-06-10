package org.iana.notifications.template.simple;

import org.iana.notifications.PContent;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateInstantiationException;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SimpleTemplate implements Template {

    private String subjectTemplate;

    private String bodyTemplate;

    private StringTemplateAlgorithm algorithm;

    private AddresseeProducer addresseeProducer;

    public SimpleTemplate(String subjectTemplate, String bodyTemplate, StringTemplateAlgorithm algorithm) {
        CheckTool.checkNull(subjectTemplate, "subject template string");
        CheckTool.checkNull(bodyTemplate, "body template string");
        CheckTool.checkNull(algorithm, "string template algorithm");
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
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


    public AddresseeProducer getAddresseeProducer() {
        return addresseeProducer;
    }

    public void setAddresseeProducer(AddresseeProducer addresseeProducer) {
        this.addresseeProducer = addresseeProducer;
    }

}
