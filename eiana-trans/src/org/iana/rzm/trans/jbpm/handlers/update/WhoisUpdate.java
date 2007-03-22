/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.jbpm.handlers.update;

import org.iana.rzm.trans.change.*;
import org.iana.rzm.trans.jbpm.handlers.UpdateDomainException;
import org.iana.rzm.domain.Domain;

import java.util.List;

public class WhoisUpdate {
    Domain domain;

    public WhoisUpdate(List<Change> changes, Domain domain) throws Exception {
        this.domain = domain;
        for(Change change : changes) {
            if(change instanceof Addition)
                visitAddition((Addition)change);
            else
            if (change instanceof Removal)
                visitRemoval((Removal)change);
            else
                visitModification((Modification)change);
        }
    }

    public void visitAddition(Addition add) throws Exception {
        PrimitiveValue<Addition> val = (PrimitiveValue<Addition>) add.getValue();
        if(domain.getWhoisServer() == null)
            domain.setWhoisServer(val.getValue());
        else
           throw new UpdateDomainException("whois value not equal with DB value");
    }

    public void visitRemoval(Removal rem) throws Exception {
        PrimitiveValue<Removal> val = (PrimitiveValue<Removal>) rem.getValue();
        if(domain.getWhoisServer() != null && domain.getWhoisServer().equals(val.getValue()))
            domain.setWhoisServer(null);
        else
           throw new UpdateDomainException("whois value not equal with DB value");
    }

    public void visitModification(Modification mod) throws Exception {
        ModifiedPrimitiveValue val = (ModifiedPrimitiveValue) mod.getValue();
        if(domain.getWhoisServer() != null && domain.getWhoisServer().equals(val.getOldValue()))
            domain.setWhoisServer(val.getNewValue());
        else
            throw new UpdateDomainException("whois value not equal with DB value");
    }

}
