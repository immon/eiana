package org.iana.rzm.trans.notifications;

import org.iana.notifications.Content;
import org.iana.notifications.ContentFactory;
import org.iana.rzm.trans.TransactionData;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */

public interface TransactionContentFactory extends ContentFactory {

    public Content createContent(String name, Map<String, String> values, TransactionData td);
}
