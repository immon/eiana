package org.iana.rzm.web.tapestry.editors;

public interface SubDomainAttributeEditor extends AttributesEditor {
    public void save(String registryUrl, String whois);
}
