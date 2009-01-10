package org.iana.rzm.web.admin.pages;

import org.iana.rzm.web.common.pages.*;

public abstract class SecureIdNextCode extends BaseSecureIdNextCode {

    protected  String getCookieName(){
        return Login.ADMIN_COOKIE_NAME;
    }
 }
