/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications;

import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.notifications.exception.InitializationNotificationException;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.validators.CheckTool;
import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;

import java.io.*;

public class NotificationTemplateManager {

    private static final String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><iana:templates xmlns:iana=\"todo\">";
    private static final String END = "</iana:templates>";

    private NotificationTemplateMapper mapper = null;
    private Config templateConfig;

    Environment env;
    DynaXMLParser parser;


    public NotificationTemplateManager(String templateProperties, String templateConfigFile) throws NotificationException {
        CheckTool.checkEmpty(templateProperties, "template properties cannot be null");
        CheckTool.checkEmpty(templateConfigFile, "template config file cannot be null");

        try {
            env = DPConfig.getEnvironment(templateProperties);
            parser = new DynaXMLParser();

            mapper = (NotificationTemplateMapper) parser.fromXML(createReader(templateConfigFile), env);

        } catch (DynaXMLException e) {
            throw new InitializationNotificationException("Initialization error: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new InitializationNotificationException("Initialization error: " + e.getMessage(), e);
        }
    }

    public void setConfig(ParameterManager manager) throws ConfigException {
        templateConfig = new OwnedConfig(manager).getSubConfig(getClass().getSimpleName());
    }

    public NotificationTemplate getNotificationTemplate(String type) throws NotificationException {
        try {
            if (templateConfig != null) {
                String content = templateConfig.getParameter(type);
                if (content != null && content.trim().length() != 0) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(HEAD).append(content).append(END);
                    NotificationTemplateMapper tempMap = (NotificationTemplateMapper) parser.fromXML(sb.toString(), env);
                    return tempMap.getNotificationTemplate(type);
                }
            }
            return mapper.getNotificationTemplate(type);
        } catch (ConfigException e) {
            throw new NotificationException(e);
        } catch (DynaXMLException e) {
            throw new NotificationException(e);
        }
    }

    private Reader createReader(String filename) throws UnsupportedEncodingException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (in != null) {
            return new InputStreamReader(in, "UTF-8");
        }
        try {
            return new FileReader(filename);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Unable to open: " + filename);
        }
    }
}
