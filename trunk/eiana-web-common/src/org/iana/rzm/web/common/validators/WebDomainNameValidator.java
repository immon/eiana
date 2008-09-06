package org.iana.rzm.web.common.validators;

import org.apache.tapestry.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.form.validator.*;
import org.apache.tapestry.valid.*;
import org.iana.commons.*;
import org.iana.dns.validator.*;

public class WebDomainNameValidator implements Validator {

    public void validate(IFormComponent field, ValidationMessages messages, Object object) throws ValidatorException {
        String domainName = (String) object;
        if(StringUtil.isBlank(domainName)){
            throw new ValidatorException("Invalid Domain name ", ValidationConstraint.REQUIRED);
        }

        try{
            String name = object.toString();
            if(name.endsWith(".")){
                name = name.substring(0, name.length() - 1);
            }
            DomainNameValidator.validateName(name);
        }catch(InvalidDomainNameException e){
            throw new ValidatorException("Invalid Domain name " + e.getName() + " " + e.getReason());
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
