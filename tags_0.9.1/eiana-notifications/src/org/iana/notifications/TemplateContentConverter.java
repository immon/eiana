package org.iana.notifications;

import org.iana.notifications.exception.NotificationException;
import org.iana.notifications.exception.TemplateNotSupportedException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.pgp.PGPUtils;
import org.iana.pgp.PGPUtilsException;

import java.io.*;

/**
 * @author Piotr Tkaczyk
 */

public class TemplateContentConverter implements ContentConverter {

    private NotificationTemplateManager templateManager;

    public TemplateContentConverter(NotificationTemplateManager manager) {
        CheckTool.checkNull(manager, "notification template manager cannot be null");
        templateManager = manager;
    }

    public String createSubject(TemplateContent templateContent) throws NotificationException {
        CheckTool.checkNull(templateContent, "null templateContent");
        NotificationTemplate template = templateManager.getNotificationTemplate(templateContent.getTemplateName());
        if (template == null) throw new TemplateNotSupportedException(templateContent.getTemplateName());
        return TemplateFiller.fill(template.getSubject(), templateContent.getValues());
    }

    private String loadResource(String name) throws IOException {
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

    public String createBody(TemplateContent templateContent) throws NotificationException {
        CheckTool.checkNull(templateContent, "null templateContent");
        NotificationTemplate template = templateManager.getNotificationTemplate(templateContent.getTemplateName());
        if (template == null) throw new TemplateNotSupportedException(templateContent.getTemplateName());
        String body = TemplateFiller.fill(template.getContent(), templateContent.getValues());
        try {
            if (template.isSigned()) {
                String key = loadResource(template.getKeyFileName());
                return PGPUtils.signMessage(body, key, template.getKeyPassphrase());
            } else return body;
        } catch (IOException e) {
            throw new NotificationException("while loading pgp key", e);
        } catch (PGPUtilsException e) {
            throw new NotificationException("while signing body", e);
        }
    }

}
