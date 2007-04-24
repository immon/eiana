/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications;

import org.iana.notifications.exception.InitializationNotificationException;
import org.iana.notifications.exception.NotificationException;
import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;

import java.io.*;

public class NotificationTemplateManager {
    private final static String IANA_TEMPLATES_PROPERTIES = "../eiana-notifications/etc/iana-templates.properties";
    private final static String IANA_TEMPLATES_CONFIG = "../eiana-notifications/etc/templates.xml";

    private NotificationTemplateMapper mapper = null;

    private static NotificationTemplateManager ourInstance = null;

    public static NotificationTemplateManager getInstance() throws NotificationException {
        return ourInstance != null ? ourInstance : (ourInstance = new NotificationTemplateManager());
    }
     
    private NotificationTemplateManager() throws NotificationException {

        try {
            Environment env = DPConfig.getEnvironment(IANA_TEMPLATES_PROPERTIES);
            DynaXMLParser parser = new DynaXMLParser();

            mapper = (NotificationTemplateMapper) parser.fromXML(createReader(IANA_TEMPLATES_CONFIG), env);

        } catch (DynaXMLException e) {
            throw new InitializationNotificationException("Initialization error: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new InitializationNotificationException("Initialization error: " + e.getMessage(), e);
        }
    }

    public NotificationTemplate getNotificationTemplate(String type) {

        return mapper.getNotificationTemplate(type);
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
