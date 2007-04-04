package org.iana.notifications;

import java.util.Map;
import java.util.Date;
import java.util.Set;
import java.text.SimpleDateFormat;

/**
 * @author Piotr Tkaczyk
 */

public class TemplateFiller {

    public static String fill(String template, Map<String, String> data) {
        Set<String> keys = data.keySet();
        for(String key : keys)
            template=template.replaceAll("\\{" + key + "\\}", generateValue(key, data.get(key)));
        return template;
    }

    private static String generateValue(String key, String value) {
        return (value == null)? "" : (!key.endsWith("Date"))? value : formDate(value);
    }

    private static String formDate(String value) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(new Long(value)));
    }
}
