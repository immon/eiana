package org.iana.rzm.web.common.query.resolver;

public class DomainFieldNameResolver implements FieldNameResolver {
    public String resolve(String fieldName) {
        if(fieldName.equals("domainName")){
            return "name.name";
        }

        return fieldName;
    }
}
