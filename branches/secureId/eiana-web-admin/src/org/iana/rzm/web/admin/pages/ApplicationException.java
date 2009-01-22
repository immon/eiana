package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.pages.*;

public abstract class ApplicationException extends BaseApplicationException {

    @InjectPage(Home.PAGE_NAME)
    public abstract Home getHomePage();

    @InjectPage(Login.PAGE_NAME)
    public abstract Login getLoginPage();
}
