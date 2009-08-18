package org.iana.notifications.template.pgp;

import org.iana.notifications.PContent;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateInstantiationException;
import org.iana.pgp.PGPUtils;
import org.iana.pgp.PGPUtilsException;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PGPTemplate implements Template {

    private Template template;

    private String key;

    private String keyPassphrase;

    public PGPTemplate(Template template, String key, String keyPassphrase) {
        CheckTool.checkNull(template, "template");
        this.template = template;
        this.key = key;
        this.keyPassphrase = keyPassphrase;
    }

    public PContent instantiate(Object data) throws TemplateInstantiationException {
        PContent content = template.instantiate(data);
        return new PContent(content.getSubject(), sign(content.getBody()));
    }

    public AddresseeProducer getAddresseeProducer() {
        return template.getAddresseeProducer();
    }

    public void setAddresseeProducer(AddresseeProducer producer) {
        template.setAddresseeProducer(producer);
    }

    public String getMailSenderType() {
        return template.getMailSenderType();
    }

    private String sign(String text) throws TemplateInstantiationException {
        try {
            return PGPUtils.signMessage(text, key, keyPassphrase);
        } catch (PGPUtilsException e) {
            throw new TemplateInstantiationException("pgp signing failed", e);
        }
    }

}
