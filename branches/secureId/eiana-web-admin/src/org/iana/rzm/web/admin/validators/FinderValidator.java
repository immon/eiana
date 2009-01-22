package org.iana.rzm.web.admin.validators;

import org.iana.rzm.web.common.query.finder.*;


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
