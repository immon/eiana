package org.iana.rzm.web.admin.validators;

import org.apache.commons.lang.*;
import org.iana.rzm.web.common.query.finder.*;


public class DomainFinderValidator implements FinderValidator {

    public void validate(String term) throws FinderValidationException {

        //try{
        //    DomainNameValidator.validateName(term);
        //}catch(InvalidDomainNameException e){
        //    throw new FinderValidationException(e.getMessage());
        //}

        if(StringUtils.isBlank(term)){
            throw new FinderValidationException("Domain Name can't be empty");            
        }
    }
}
