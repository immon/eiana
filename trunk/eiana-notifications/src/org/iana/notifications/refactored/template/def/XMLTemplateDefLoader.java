package org.iana.notifications.refactored.template.def;

import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;

import java.io.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class XMLTemplateDefLoader implements TemplateDefConfig {

    TemplateDefConfig config;

    public XMLTemplateDefLoader(String properties, String config) throws TemplateInitializationException {
        try {
            Environment env = DPConfig.getEnvironment(properties);
            DynaXMLParser parser = new DynaXMLParser();
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(config);
            if (in == null) throw new FileNotFoundException("cannot load resource " + config);
            Reader reader = new InputStreamReader(in, "UTF-8");
/*
            BufferedReader buf = new BufferedReader(reader);
            String line, total = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
                total += line;
            }
*/
            this.config = (TemplateDefConfig) parser.fromXML(reader, env);
            // reader.close();
        } catch (DynaXMLException e) {
            throw new TemplateInitializationException(e);
        } catch (FileNotFoundException e) {
            throw new TemplateInitializationException(e);
        } catch (UnsupportedEncodingException e) {
            throw new TemplateInitializationException(e);
        } catch (IOException e) {
            throw new TemplateInitializationException(e);
        }
    }

    public TemplateDef getTemplateDef(String name) {
        return config.getTemplateDef(name);
    }

}
