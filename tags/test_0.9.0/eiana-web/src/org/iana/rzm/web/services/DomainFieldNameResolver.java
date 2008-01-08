package org.iana.rzm.web.services;

public class DomainFieldNameResolver implements FieldNameResolver {
    public String resolve(String fieldName) {
        if(fieldName.equals("domainName")){
            return "name.name";
        }

        return fieldName;
    }
}
