package org.iana.rzm.web.admin.services;

import org.iana.rzm.facade.admin.msgs.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.query.resolver.*;

import java.util.*;

public class PollMsgFieldNameResolver implements FieldNameResolver {

    private Map<String, String> fields;

    public PollMsgFieldNameResolver() {
        fields = new HashMap<String, String>();
        fields.put("Ref", TransactionCriteriaFields.TICKET_ID);
        fields.put("ID", PollMsgFields.ID);
        fields.put("Domain", PollMsgFields.DOMAIN_NAME);
        fields.put("Lodge", PollMsgFields.CREATED);
        fields.put("Logged", PollMsgFields.CREATED);
    }

    public String resolve(String fieldName) {
        String name = fields.get(fieldName);
        return name == null ? fieldName : name;
    }
}
