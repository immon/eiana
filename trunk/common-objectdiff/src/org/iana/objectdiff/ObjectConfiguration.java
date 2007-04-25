package org.iana.objectdiff;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ObjectConfiguration {

    private List<String> fields = new ArrayList();
    private Map<String, ObjectInstantiator> classes = new HashMap<String, ObjectInstantiator>();
    private String id;

    public ObjectConfiguration(String[] fields, String id) {
        for (String field : fields) this.fields.add(field);
        this.id = id;
    }

    public ObjectConfiguration(List<String> fields, String id) {
        if (fields != null) this.fields.addAll(fields);
        this.id = id;
    }

    public void addFieldInstantiator(String field, ObjectInstantiator inst) {
        if (inst == null) throw new IllegalArgumentException("null object instantiator");
        classes.put(field, inst);
    }

    public void addFieldClass(String field, Class clazz) {
        classes.put(field, new ClassBasedInstantiator(clazz));
    }

    public ObjectInstantiator getFieldInstantiator(String field) {
        return classes.get(field);
    }
    
    public List<String> getFields() {
        return fields;
    }

    public String getFieldId() {
        return id;
    }
}
