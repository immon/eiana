package org.iana.rzm.web.common.admin;

import org.iana.rzm.web.common.*;


/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Jul 18, 2007
 * Time: 2:00:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FinderValidator {

    public void validate(String term)throws FinderValidationException;
}
