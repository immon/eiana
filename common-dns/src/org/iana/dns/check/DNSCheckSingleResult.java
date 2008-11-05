package org.iana.dns.check;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public class DNSCheckSingleResult implements DNSCheckResult {

    private DNSTechnicalCheckException exception;

    private String checkName;

    public DNSCheckSingleResult(String checkName, DNSTechnicalCheckException exception) {
        this.checkName = checkName;
        this.exception = exception;
    }

    public DNSCheckSingleResult(String checkName) {
        this.checkName = checkName;
        this.exception = null;
    }

    public DNSTechnicalCheckException getException() {
        return exception;
    }

    public boolean isSuccess() {
        return (exception == null);
    }

    public List<String> getSuccessfulCheckNames() {
        return getCheckNames(true);
    }

    public List<String> getFailedCheckNames() {
        return getCheckNames(false);
    }

    private List<String> getCheckNames(boolean successful) {
        return (isSuccess() == successful) ? Arrays.asList(checkName) : new ArrayList<String>();
    }
}
