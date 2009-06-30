package org.iana.rzm.web.tapestry.editors;

import org.iana.rzm.web.common.model.NameServerVOWrapper;
import org.iana.rzm.web.common.model.NameServerValue;

import java.util.List;

public interface NameServerAttributesEditor extends AttributesEditor {
    public void save(List<NameServerValue> list);
    public List<NameServerVOWrapper> getOriginalNameServerList(long domainId);
}
