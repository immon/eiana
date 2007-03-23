package org.iana.rzm.trans.change;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ObjectConfiguration {

    private List<String> fields = new ArrayList();
    private Map<String, Class> classes = new HashMap<String, Class>();
    private String id;

    public ObjectConfiguration(String[] fields, String id) {
        for (String field : fields) this.fields.add(field);
        this.id = id;
    }

    public ObjectConfiguration(List<String> fields, String id) {
        if (fields != null) this.fields.addAll(fields);
        this.id = id;
    }

    public void addFieldClass(String field, Class clazz) {
        classes.put(field, clazz);
    }

    public Class getFieldClass(String field) {
        return classes.get(field);
    }
    
    public List<String> getFields() {
        return fields;
    }

    public String getFieldId() {
        return id;
    }
}
