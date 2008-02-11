package org.iana.notifications.refactored.template.simple;

import org.iana.rzm.common.validators.CheckTool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

public abstract class TemplateFiller {

    public static String fill(String template, Map<String, ?> data) {
        CheckTool.checkNull(data, "template content data");
        if (data.isEmpty()) return template;
        Set<String> keys = data.keySet();
        for (String key : keys)
            template = template.replaceAll("\\{" + key + "\\}", escapeDollars(generateValue(key, data.get(key))));
        return template;
    }

    private static String escapeDollars(String value) {
        StringBuffer result = new StringBuffer();
        String[] splitted = value.split("\\$");
        result.append(splitted[0]);
        for (int i = 1; i < splitted.length; i++)
            result.append("\\$").append(splitted[i]);
        return result.toString();
    }

    private static String generateValue(String key, Object object) {
        String value = object == null ? null : object.toString();
        return (value == null) ? "" : (!key.endsWith("Date")) ? value : formDate(value);
    }

    private static String formDate(String value) {
        try {
            Long longValue = new Long(value);
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(longValue));
        } catch (NumberFormatException e) {
            return "Wrong date value: " + value;
        }
    }
}
