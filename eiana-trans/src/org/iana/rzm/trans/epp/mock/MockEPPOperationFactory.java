package org.iana.rzm.trans.epp.mock;

import org.iana.epp.ChangePriority;
import org.iana.epp.EPPHostAddress;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.request.*;
import org.iana.rzm.trans.epp.mock.request.*;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MockEPPOperationFactory implements EPPOperationFactory {
    public ChangeRequest getChangeRequest(String string, String string1, ChangePriority changePriority, List<String> list) {
        return new MockChangeRequest();
    }

    public CreateHost getCreateHostRequest(String string, String string1, List<EPPHostAddress> list) {
        return new MockCreateHost();
    }

    public DeleteHost getDeleteHostRequest(String string, String string1) {
        return new MockDeleteHost();
    }

    public UpdateHost getUpdateHost(String string, String string1, List<EPPHostAddress> list, List<EPPHostAddress> list1) {
        return new MockUpdateHost();
    }

    public DomainUpdate getDomainUpdateRequest(String string, String string1) {
        return new MockDomainUpdate();
    }

    public AckRequest getAckRequest(String string, String string1) {
        return new MockAckRequest();
    }

    public PollRequest getPollRequest(String string) {
        return new MockPollRequest();
    }

    public DeleteChange getDeleteChangeRequest(String string, ChangeRef changeRef) {
        return new MockDeleteChange();
    }
}
