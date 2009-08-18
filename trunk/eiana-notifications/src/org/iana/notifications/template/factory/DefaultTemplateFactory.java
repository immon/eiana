package org.iana.notifications.template.factory;

import org.apache.log4j.Logger;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateNotFoundException;
import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.def.TemplateDefConfig;
import org.iana.notifications.template.pgp.PGPTemplate;
import org.iana.notifications.template.pgp.PgpKey;
import org.iana.notifications.template.pgp.PgpKeyConfig;
import org.iana.notifications.template.simple.SimpleTemplate;
import org.iana.notifications.template.simple.StringTemplateAlgorithm;
import org.iana.rzm.common.validators.CheckTool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DefaultTemplateFactory implements TemplateFactory {

    private static Logger logger = Logger.getLogger(DefaultTemplateFactory.class);

    private Map<String, Template> templates = new HashMap<String, Template>();

    private Map<String, AddresseeProducer> producers = new HashMap<String, AddresseeProducer>();

    private TemplateDefConfig templateConfig;

    private PgpKeyConfig pgpKeyConfig;

    private StringTemplateAlgorithm defaultTemplateAlgorithm;

    public static final String KEY = "key";

    public static final String KEY_FILENAME = "keyFilename";

    public static final String KEY_PASSPHRASE = "keyPassphrase";

    public DefaultTemplateFactory(TemplateDefConfig templateConfig, PgpKeyConfig pgpKeyConfig, StringTemplateAlgorithm alg) {
        CheckTool.checkNull(templateConfig, "template def templateConfig");
        CheckTool.checkNull(pgpKeyConfig, "pgp key config");
        CheckTool.checkNull(alg, "default string template algorithm");
        this.templateConfig = templateConfig;
        this.pgpKeyConfig = pgpKeyConfig;
        this.defaultTemplateAlgorithm = alg;
    }

    public Template getTemplate(String name) throws TemplateNotFoundException {
        if (!templates.containsKey(name)) {
            Template t = initTemplate(name);
            if (t == null) throw new TemplateNotFoundException(name);
        }
        return templates.get(name);
    }

    public void setProducers(Map<String, AddresseeProducer> producers) {
        CheckTool.checkNull(producers, "producers");
        this.producers = producers;
    }

    private Template initTemplate(String name) throws TemplateNotFoundException {
        synchronized (templates) {
            if (!templates.containsKey(name)) {
                TemplateDef def = templateConfig.getTemplateDef(name);
                if (def != null) {
                    Template ret = new SimpleTemplate(def, defaultTemplateAlgorithm);
                    if (def.getAddressees() != null && !def.getAddressees().isEmpty()) {
                        ret.setAddresseeProducer(new ConfiguredRecipients(producers, def.getAddressees()));
                    }
                    if (def.isSigned()) {
                        String keyName = def.getKeyName();
                        PgpKey pgpKey = pgpKeyConfig.getPgpKey(keyName);

                        if (pgpKey == null) {
                            logger.warn("can't initialize template - pgp key loading failed for key name: " + keyName);
                            return null;
                        }

                        ret = new PGPTemplate(ret, pgpKey.getArmouredKey(), pgpKey.getPassphrase());
                    }
                    templates.put(name, ret);
                    return ret;
                } else {
                    throw new TemplateNotFoundException(name);
                }
            }
        }

        return null;
    }
}
