package org.iana.rzm.web.tapestry.editors;

import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.tapestry.editors.AttributesEditor;

import java.util.*;

public interface NameServerAttributesEditor extends AttributesEditor {
    public void save(List<NameServerValue> list);
    public List<NameServerVOWrapper> getOriginalNameServerList(long domainId);
}
