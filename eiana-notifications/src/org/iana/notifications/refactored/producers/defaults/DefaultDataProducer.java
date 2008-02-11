package org.iana.notifications.refactored.producers.defaults;

import org.iana.notifications.refactored.producers.DataProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DefaultDataProducer implements DataProducer {

    public Map<String, String> getValuesMap(Map dataSource) {
        Map<String, String> ret = new HashMap<String, String>();
        for (Object key : dataSource.keySet()) {
            String skey = String.valueOf(key);
            String svalue = String.valueOf(dataSource.get(key));
            ret.put(skey, svalue);
        }
        return ret;
    }
}
