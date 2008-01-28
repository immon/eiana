package org.iana.rzm.trans.notifications;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.JbpmContext;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionData;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Piotr Tkaczyk
 */
public class NotificationDataSource implements Map {

    private ExecutionContext executionContext;
    private Map<String, Object> additionalData;

    public NotificationDataSource(ExecutionContext executionContext, Map<String, Object> additionalData) {
        CheckTool.checkNull(executionContext, "execution context is null");
        CheckTool.checkNull(additionalData, "additional data is null");
        this.executionContext = executionContext;
        this.additionalData = additionalData;
    }


    public NotificationDataSource(ExecutionContext executionContext) {
        new NotificationDataSource(executionContext, new HashMap<String, Object>());
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object o) {
        return (get(o) != null);
    }

    public boolean containsValue(Object o) {
        throw new UnsupportedOperationException();
    }

    public Object get(Object o) {
        return (o instanceof String)? getObject((String)o) : null;
    }

    private Object getObject(String key) {
        try{
            ObjectFactory objectFactory = executionContext.getJbpmContext().getObjectFactory();

            if (objectFactory.hasObject(key))
                return objectFactory.createObject(key);

            ProcessInstance processInstance = executionContext.getProcessInstance();

            if (processInstance.getRootToken() == null) return null;
            if ("stateName".equals(key))
                return processInstance.getRootToken().getNode().getName();

            if ("transactionId".equals(key))
                return processInstance.getId();

            if ("receipt".equals(key)) {
                if (processInstance.getContextInstance().hasVariable("TRANSACTION_DATA")) {
                    TransactionData td = (TransactionData)
                            processInstance.getContextInstance().getVariable("TRANSACTION_DATA");
                    return td.getEppReceipt();
                } else return null;
            }

            if ("eppID".equals(key)) {
                if (processInstance.getContextInstance().hasVariable("TRANSACTION_DATA")) {
                    TransactionData td = (TransactionData)
                            processInstance.getContextInstance().getVariable("TRANSACTION_DATA");
                    return td.getEppRequestId();
                } else return null;
            }

            Object obj = executionContext.getContextInstance().getVariable(key);
            if (obj != null) return obj;
            return additionalData.get(key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Object put(Object o, Object o1) {
        return additionalData.put((String)o, o1);
    }

    public Object remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    public Collection values() {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {
        throw new UnsupportedOperationException();
    }
}
