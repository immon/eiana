package org.iana.rzm.web.tapestry.editors;

import org.iana.rzm.web.tapestry.editors.AttributesEditor;

public interface SubDomainAttributeEditor extends AttributesEditor {
    public void save(String registryUrl, String whois);
}
