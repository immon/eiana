package org.iana.rzm.web.common.validators;

import org.apache.tapestry.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.form.validator.*;
import org.apache.tapestry.valid.*;
import org.iana.codevalues.*;
import org.iana.rzm.web.common.*;

public class WebCountryCodeValidator implements Validator {

    public static final Value NO_COUNTRY = new Value("NONE", "Country");

    public void validate(IFormComponent field, ValidationMessages messages, Object object) throws ValidatorException {
        String msg = new MessageUtil().getNoCountryErrorMessage();
        Value value = (Value) object;
        if (value.getValueId().equals(NO_COUNTRY.getValueId())) {
            throw new ValidatorException(msg, ValidationConstraint.REQUIRED);
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