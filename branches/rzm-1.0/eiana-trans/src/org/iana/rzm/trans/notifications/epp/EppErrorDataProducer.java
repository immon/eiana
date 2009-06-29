package org.iana.rzm.trans.notifications.epp;

import org.iana.notifications.producers.DataProducer;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Piotr Tkaczyk
 */
public class EppErrorDataProducer implements DataProducer {

    public Map<String, String> getValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();
        String msg = (String) dataSource.get("exception");

        values.put("exception", msg);

        return values;
    }
}
