package org.iana.rzm.web.common.admin;

import org.apache.commons.lang.*;
import org.iana.rzm.web.common.*;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Jul 19, 2007
 * Time: 10:56:36 AM
 * To change this template use File | Settings | File Templates.
 */
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
