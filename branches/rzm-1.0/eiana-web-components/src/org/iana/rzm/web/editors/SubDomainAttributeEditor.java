package org.iana.rzm.web.editors;

public interface SubDomainAttributeEditor extends AttributesEditor {
    public void save(String registryUrl, String whois);
}
