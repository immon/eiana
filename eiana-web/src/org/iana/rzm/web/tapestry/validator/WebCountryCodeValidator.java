package org.iana.rzm.web.tapestry.validator;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.ValidationMessages;
import org.apache.tapestry.form.validator.Validator;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidatorException;
import org.iana.codevalues.Value;
import org.iana.rzm.web.components.ContactEditor;

public class WebCountryCodeValidator implements Validator {

    public void validate(IFormComponent field, ValidationMessages messages, Object object) throws ValidatorException {
        Value value = (Value) object;
        if (value.getValueId().equals(ContactEditor.NO_COUNTRY.getValueId())) {
            throw new ValidatorException("Please select a country", ValidationConstraint.REQUIRED);
        }
    }

    public boolean getAcceptsNull() {
        return false;
    }

    public boolean isRequired() {
        return false;
    }

    public void renderContribution(IMarkupWriter writer,
                                   IRequestCycle cycle,
                                   FormComponentContributorContext context,
                                   IFormComponent field) {

    }
}