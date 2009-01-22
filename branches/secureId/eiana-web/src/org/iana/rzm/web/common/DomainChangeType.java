package org.iana.rzm.web.common;

public enum DomainChangeType {

    Administrative("Administrative"),
    Technical("Technical"),
    SO("Sponsoring Organization"),
    ns("ns"),
    sudomain("subdomain"),
    admin("admin");
    private String displayName;

    DomainChangeType(String displayName) {
        this.displayName = displayName;
    }

    public static DomainChangeType fromString(String type) {

        if (Administrative.displayName.equals(type)) {
            return Administrative;
        } else if (Technical.displayName.equals(type)) {
            return Technical;
        } if (SO.displayName.equals(type)) {
            return SO;
        } else if (sudomain.displayName.equals(type)) {
            return sudomain;
        } else if (admin.displayName.equals(type)) {
            return admin;
        }
        throw new  IllegalArgumentException("Unknow type " + type);

    }

    public String getDisplayName() {
        return displayName;
    }
}
