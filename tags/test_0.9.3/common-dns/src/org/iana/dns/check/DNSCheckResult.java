package org.iana.dns.check;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface DNSCheckResult {

    boolean isSuccess();

    DNSTechnicalCheckException getException();

    List<String> getSuccessfulCheckNames();

    List<String> getFailedCheckNames();
}
