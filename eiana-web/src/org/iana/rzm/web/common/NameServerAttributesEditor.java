package org.iana.rzm.web.common;

import org.iana.rzm.web.model.NameServerValue;

import java.util.List;

public interface NameServerAttributesEditor extends AttributesEditor {
    public void save(List<NameServerValue> list);
}
