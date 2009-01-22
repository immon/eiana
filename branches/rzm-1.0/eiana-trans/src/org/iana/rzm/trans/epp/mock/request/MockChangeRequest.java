package org.iana.rzm.trans.epp.mock.request;

import org.iana.epp.ChangePriority;
import org.iana.epp.request.ChangeRequest;
import org.iana.epp.request.EPPChange;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MockChangeRequest extends MockRequest implements ChangeRequest {
    private List<EPPChange> changes = new ArrayList<EPPChange>();

    public List<String> getEffectedDomains() {
        return new ArrayList<String>();
    }

    public ChangePriority getPriority() {
        return null;
    }

    public void addChange(EPPChange eppChange) {
        changes.add(eppChange);
    }

    public void addChange(int i, EPPChange eppChange) {
        changes.set(i, eppChange);
    }

    public List<EPPChange> getChanges() {
        return changes;
    }

    public String getRequestId() {
        return "";
    }

    public String getDescription() {
        return "";
    }
}
