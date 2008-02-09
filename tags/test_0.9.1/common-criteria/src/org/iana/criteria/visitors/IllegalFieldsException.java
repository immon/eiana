package org.iana.criteria.visitors;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IllegalFieldsException extends IllegalArgumentException {

    private Set<String> illegalFields;

    public IllegalFieldsException(Set<String> illegalFields) {
        this.illegalFields = illegalFields;
    }

    public Set<String> getIllegalFields() {
        return illegalFields;
    }
}
