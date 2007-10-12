package org.iana.notifications;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface ContentFactory {

    public Content createContent(String name);

    public Content createContent(String name, Map<String, String> values);
}
