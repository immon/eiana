package org.iana.criteria.visitors;

import org.iana.criteria.CriteriaVisitor;
import org.iana.criteria.FieldVisitor;
import org.iana.criteria.FieldCriterion;
import org.iana.criteria.Criterion;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;

/**
 * This visitor validates whether the criteria contains only the allowed field names.
 *
 * @author Patrycja Wegrzynowicz
 */
public class FieldsValidator extends FieldVisitor {

    Set<String> allowedFieldNames;

    Set<String> errors;

    @SuppressWarnings("unchecked")
    public FieldsValidator(Collection<String> allowedFieldNames) {
        this.allowedFieldNames = allowedFieldNames != null ? new HashSet<String>(allowedFieldNames) : Collections.EMPTY_SET;
        this.errors = new HashSet<String>();
    }

    public void validate(Criterion criteria) throws IllegalFieldsException {
        if (criteria != null) criteria.accept(this);
        if (!errors.isEmpty()) throw new IllegalFieldsException(errors);
    }

    protected void visitField(FieldCriterion field) {
        String fieldName = field.getFieldName();
        if (!allowedFieldNames.contains(fieldName)) {
            errors.add(fieldName);
        }
    }

}
