/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.change.ModifiedPrimitiveValue;
import org.iana.rzm.trans.change.Modification;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionAction;
import org.iana.rzm.trans.jbpm.handlers.UpdateDomainException;


import java.net.URL;
import java.util.List;

public class UpdateDomainAction implements ActionHandler {
    public void execute(org.jbpm.graph.exe.ExecutionContext executionContext) throws java.lang.Exception {

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");

        Domain retrivedDomain = domainManager.get(td.getCurrentDomain().getName());

        List<TransactionAction> trActions = td.getActions();

        for (TransactionAction ta : trActions ) {
            if (ta.getName().equals(TransactionAction.Name.MODIFY_WHOIS_SERVER)) {
                List<Change> changes = ta.getChange();
                for (Change change : changes) {
                    ModifiedPrimitiveValue primitiveValue = (ModifiedPrimitiveValue)((Modification) change).getValue();
                    if(!retrivedDomain.getWhoisServer().equals(primitiveValue.getOldValue()))
                        throw new UpdateDomainException("old whois value not equal with DB value");
                    retrivedDomain.setWhoisServer(primitiveValue.getNewValue());
                }
            } else
            if (ta.getName().equals(TransactionAction.Name.MODIFY_REGISTRATION_URL)) {
                List<Change> changes = ta.getChange();
                for (Change change : changes) {
                    ModifiedPrimitiveValue primitiveValue = (ModifiedPrimitiveValue)((Modification) change).getValue();
                    if(!retrivedDomain.getRegistryUrl().toString().equals(primitiveValue.getOldValue()))
                        throw new UpdateDomainException("old registryUrl value not equal with DB value");
                    retrivedDomain.setRegistryUrl(new URL(primitiveValue.getNewValue()));
                }
            }
        }
        domainManager.update(retrivedDomain);
    }
}
