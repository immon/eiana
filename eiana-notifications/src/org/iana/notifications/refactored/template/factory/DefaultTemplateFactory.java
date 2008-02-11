package org.iana.notifications.refactored.template.factory;

import org.apache.log4j.Logger;
import org.iana.notifications.refactored.template.Template;
import org.iana.notifications.refactored.template.def.TemplateDef;
import org.iana.notifications.refactored.template.def.TemplateDefConfig;
import org.iana.notifications.refactored.template.pgp.PGPTemplate;
import org.iana.notifications.refactored.template.simple.SimpleTemplate;
import org.iana.notifications.refactored.template.simple.StringTemplateAlgorithm;
import org.iana.rzm.common.validators.CheckTool;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DefaultTemplateFactory implements TemplateFactory {

    private static Logger logger = Logger.getLogger(DefaultTemplateFactory.class);

    private Map<String, Template> templates = new HashMap<String, Template>();

    private TemplateDefConfig config;

    private StringTemplateAlgorithm defaultTemplateAlgorithm;

    public DefaultTemplateFactory(TemplateDefConfig config, StringTemplateAlgorithm alg) {
        CheckTool.checkNull(config, "template def config");
        CheckTool.checkNull(alg, "default string template algorithm");
        this.config = config;
        this.defaultTemplateAlgorithm = alg;
    }

    public Template getTemplate(String name) {
        if (!templates.containsKey(name)) {
            return initTemplate(name);
        }
        return templates.get(name);
    }

    private Template initTemplate(String name) {
        try {
            synchronized (templates) {
                if (!templates.containsKey(name)) {
                    TemplateDef def = config.getTemplateDef(name);
                    if (def != null) {
                        Template ret = new SimpleTemplate(def.getSubject(), def.getContent(), defaultTemplateAlgorithm);
                        if (def.isSigned()) {
                            ret = new PGPTemplate(ret, load(def.getKeyFileName()), def.getKeyPassphrase());
                        }
                        templates.put(name, ret);
                        return ret;
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("can't initialize template - pgp key loading failed", e);
        }
        return null;
    }

    private String load(String name) throws IOException {
        // reused piotrt's code
        InputStream in = getClass().getClassLoader().getResourceAsStream(name);
        if (in == null) throw new FileNotFoundException(name);
        DataInputStream dis = new DataInputStream(in);
        StringBuffer buf;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(dis, "US-ASCII"));
            try {
                buf = new StringBuffer();
                String line = reader.readLine();
                if (line != null) {
                    buf.append(line);
                    while ((line = reader.readLine()) != null) {
                        buf.append("\n");
                        buf.append(line);
                    }
                }
                return buf.toString();
            } finally {
                reader.close();
            }
        } finally {
            dis.close();
        }
    }
}
