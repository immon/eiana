package org.iana.rzm.web.common;

import org.iana.rzm.web.model.*;

import java.util.*;

public interface NameServerAttributesEditor extends AttributesEditor {
    public void save(List<NameServerValue> list);
    public List<NameServerVOWrapper> getOriginalNameServerList(long domainId);
}
