package org.iana.rzm.trans.epp.mock.response;

import org.iana.epp.ChangePriority;
import org.iana.epp.response.ChangeAction;
import org.iana.epp.response.ChangeInfoResponse;

import java.util.Date;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MockChangeInfoResponse extends MockResponse implements ChangeInfoResponse {
    public Date getCreatedDate() {
        return null;
    }

    public Date getUpdatedDate() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public List<String> getDomains() {
        return null;
    }

    public String getStatus() {
        return null;
    }

    public List<ChangeAction> getChanges() {
        return null;
    }

    public ChangePriority getPriority() {
        return null;
    }
}
