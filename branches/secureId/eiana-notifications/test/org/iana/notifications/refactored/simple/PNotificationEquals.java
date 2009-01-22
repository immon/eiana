package org.iana.notifications.refactored.simple;

import static org.easymock.EasyMock.reportMatcher;
import org.easymock.IArgumentMatcher;
import org.iana.notifications.PNotification;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PNotificationEquals implements IArgumentMatcher {

    private PNotification expected;

    public PNotificationEquals(PNotification expected) {
        this.expected = expected;
    }

    public boolean matches(Object argument) {
        if (argument instanceof PNotification) {
            PNotification actual = (PNotification) argument;
            return expected.getType().equals(actual.getType()) &&
                    expected.getAddressees().equals(actual.getAddressees()) &&
                    expected.getContent().equals(actual.getContent()) &&
                    expected.isPersistent() == actual.isPersistent();
        }
        return false;
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("expected PNotification with ")
                .append(expected.getType()).append(", ")
                .append(expected.getAddressees()).append(", ")
                .append(expected.getContent()).append(", ")
                .append(expected.isPersistent());
    }

    public static PNotification eqNotifications(PNotification in) {
        reportMatcher(new PNotificationEquals(in));
        return null;
    }
}
