package org.iana.rzm.trans.epp.mock.response;

import org.iana.epp.Problem;
import org.iana.epp.Result;
import org.iana.epp.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MockResponse implements Response {
    public Result getResultAt(int i) {
        return null;
    }

    public boolean isSuccessful() {
        return true;
    }

    public boolean hasErrors() {
        return false;
    }

    public List<Problem> getErrors() {
        return new ArrayList<Problem>();
    }
}
