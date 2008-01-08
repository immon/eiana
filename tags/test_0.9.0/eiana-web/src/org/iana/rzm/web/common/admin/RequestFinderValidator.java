package org.iana.rzm.web.common.admin;

import org.iana.rzm.web.common.*;
import org.iana.rzm.web.util.*;

public class RequestFinderValidator implements FinderValidator{

    public void validate(String term) throws FinderValidationException {
        MessageUtil messageUtil = new MessageUtil();
        try{
            Long.parseLong(term);
        }catch(NumberFormatException e){
            throw new FinderValidationException(messageUtil.getRequestFinderValidationErrorMessage(term));
        }
    }
}
