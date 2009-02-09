package org.iana.notifications.template.factory;

import org.apache.log4j.Logger;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.def.TemplateDefConfig;
import org.iana.notifications.template.pgp.PGPTemplate;
import org.iana.notifications.template.simple.SimpleTemplate;
import org.iana.notifications.template.simple.StringTemplateAlgorithm;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DefaultTemplateFactory implements TemplateFactory {

    private static Logger logger = Logger.getLogger(DefaultTemplateFactory.class);

    private Map<String, Template> templates = new HashMap<String, Template>();

    private TemplateDefConfig templateConfig;

    private Config config;

    private StringTemplateAlgorithm defaultTemplateAlgorithm;

    public static final String KEY = "key";

    public static final String KEY_FILENAME = "keyFilename";

    public static final String KEY_PASSPHRASE = "keyPassphrase";

    public DefaultTemplateFactory(TemplateDefConfig config, StringTemplateAlgorithm alg) {
        CheckTool.checkNull(config, "template def templateConfig");
        CheckTool.checkNull(alg, "default string template algorithm");
        this.templateConfig = config;
        this.defaultTemplateAlgorithm = alg;
    }

    public void setConfig(ParameterManager manager) throws ConfigException {
        config = new OwnedConfig(manager).getSubConfig(getClass().getSimpleName());
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
                    TemplateDef def = templateConfig.getTemplateDef(name);
                    if (def != null) {
                        Template ret = new SimpleTemplate(def.getSubject(), def.getContent(), defaultTemplateAlgorithm);
                        if (def.isSigned()) {
                            String key = getKey();
                            String keyFilename = getKeyFilename(def.getKeyFileName());
                            String keyPassphrase = getKeyPassphrase(def.getKeyPassphrase());
                            String pgpKey = key == null ? load(keyFilename) : key;
                            ret = new PGPTemplate(ret, pgpKey, keyPassphrase);
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

    private String getKey() {
        try {
            if (config != null) {
                String keyFilename = config.getParameter(KEY);
                if (keyFilename != null) {
                    return keyFilename;
                }
            }
        } catch (ConfigException e) {
        }
        return null;
    }

    private String getKeyFilename(String defaultKeyFilename) {
        try {
            if (config != null) {
                String keyFilename = config.getParameter(KEY_FILENAME);
                if (keyFilename != null) {
                    return keyFilename;
                }
            }
        } catch (ConfigException e) {
        }
        return defaultKeyFilename;
    }

    private String getKeyPassphrase(String defaultKeyPassphrase) {
        try {
            if (config != null) {
                String keyPassphrase = config.getParameter(KEY_PASSPHRASE);
                if (keyPassphrase != null) {
                    return keyPassphrase;
                }
            }
        } catch (ConfigException e) {
        }
        return defaultKeyPassphrase;
    }

    private String load(String name) throws IOException {
        // reused piotrt's code
        InputStream in = getClass().getClassLoader().getResourceAsStream(name);
        if (in == null) in = new FileInputStream(name);
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
