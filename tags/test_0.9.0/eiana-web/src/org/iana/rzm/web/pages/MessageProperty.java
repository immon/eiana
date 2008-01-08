package org.iana.rzm.web.pages;

import org.apache.tapestry.IPage;

public interface MessageProperty extends IPage {

    public void setInfoMessage(String value);

    public void setWarningMessage(String value);

    public void setErrorMessage(String value);

}
