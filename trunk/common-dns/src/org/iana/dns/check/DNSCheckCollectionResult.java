package org.iana.dns.check;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public class DNSCheckCollectionResult implements DNSCheckResult {

    List<DNSCheckResult> results;

    public DNSCheckCollectionResult() {
        results = new ArrayList<DNSCheckResult>();
    }

    public DNSCheckCollectionResult(List<DNSCheckResult> results) {
        this.results = results;
    }

    public void addResult(DNSCheckResult result) {
        results.add(result);
    }

    public boolean isSuccess() {
        for (DNSCheckResult result : results) {
            if (!result.isSuccess()) {
                return false;
            }
        }

        return true;
    }

    public MultipleDNSTechnicalCheckException getException() {
        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();

        for (DNSCheckResult result : results) {
            if (!result.isSuccess()) {
                e.addException(result.getException());
            }
        }

        return e;
    }


    public List<String> getSuccessfulCheckNames() {
        List<String> names = new ArrayList<String>();

        for (DNSCheckResult result : results) {
            names.addAll(result.getSuccessfulCheckNames());
        }

        return names;
    }

    public List<String> getFailedCheckNames() {
        List<String> names = new ArrayList<String>();

        for (DNSCheckResult result : results) {
            names.addAll(result.getFailedCheckNames());
        }

        return names;
    }
}
