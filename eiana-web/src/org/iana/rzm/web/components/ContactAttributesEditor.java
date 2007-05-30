package org.iana.rzm.web.components;

import java.util.Map;


public interface ContactAttributesEditor extends AttributesEditor {
    public void save(Map<String, String> attributes);
}
