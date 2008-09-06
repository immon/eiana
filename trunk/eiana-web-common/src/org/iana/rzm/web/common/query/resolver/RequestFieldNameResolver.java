package org.iana.rzm.web.common.query.resolver;

import org.iana.rzm.facade.system.trans.*;

import java.util.*;

public class RequestFieldNameResolver implements FieldNameResolver {

    private Map<String,String> fields;

    public RequestFieldNameResolver(){
        fields = new HashMap<String, String>();
        fields.put("Ref", TransactionCriteriaFields.TICKET_ID);
        fields.put("Domain", TransactionCriteriaFields.CURRENT_DOMAIN_NAME);
        fields.put("Lodge", TransactionCriteriaFields.CREATED);
        fields.put("Logged", TransactionCriteriaFields.CREATED);
        fields.put("Created By", TransactionCriteriaFields.CREATED_BY);
        fields.put("Current Status", TransactionCriteriaFields.STATE);
        fields.put("Last Change", TransactionCriteriaFields.MODIFIED);
    }

    public String resolve(String fieldName) {
        String name = fields.get(fieldName);
        return name == null ? fieldName : name;
    }
}
