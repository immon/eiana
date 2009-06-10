package org.iana.rzm.web.common.components;

import org.apache.tapestry.BaseComponent;
import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.pages.ProtectedPage;
import org.iana.rzm.web.common.services.RzmServices;

public abstract class RzmComponent extends BaseComponent {

    
    public RzmServices getRzmServices(){
        return getApplicationPage().getRzmServices();
    }

    public ProtectedPage getApplicationPage(){
        return (ProtectedPage) getPage();
    }

    public MessageUtil getMessageUtil(){
        return getApplicationPage().getMessageUtil();
    }

}
