package org.iana.rzm.web.common;

import java.util.Map;


public interface ContactAttributesEditor extends AttributesEditor {
    public void save(Map<String, String> attributes);
}
