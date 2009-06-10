package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.commons.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.pages.*;
import org.iana.rzm.web.common.services.*;

import java.net.*;

public abstract class AdminPage extends ProtectedPage {

    public static final IPAddressData ICANN_ADDRESS1_START = new IPAddressData("192.0.32.0");
    public static final IPAddressData ICANN_ADDRESS1_END = new IPAddressData("192.0.47.255");

    public static final IPAddressData ICANN_ADDRESS2_START = new IPAddressData("208.77.188.0");
    public static final IPAddressData ICANN_ADDRESS2_END = new IPAddressData("208.77.191.255");

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @InjectObject("service:rzm.ExternalPageErrorHandler")
    public abstract ExternalPageErrorHandler getExternalPageErrorHandler();



    public RzmServices getRzmServices() {
        return getAdminServices();
    }

    protected String getErrorPageName() {
        return getErrorPage().getPageName();
    }

    public void pageValidate(PageEvent event) {
        super.pageValidate(event);

        String ipaddress = getHttpRequest().getHeader("HTTP_X_FORWARDED_FOR");

        if (ipaddress == null) {
            ipaddress = getHttpRequest().getRemoteAddr();
        }

        if (!isLocalRequest(ipaddress)) {
            IPAddressData ip = new IPAddressData(ipaddress);

            if (!(ip.greaterThanOrEqual(ICANN_ADDRESS1_START) && ICANN_ADDRESS1_END.greaterThanOrEqual(ip) ||
                  (ip.greaterThanOrEqual(ICANN_ADDRESS2_START) && ICANN_ADDRESS2_END.greaterThanOrEqual(ip)))) {
                Login login = (Login) logout();
                login.setAdminLoginError(getMessageUtil().getAdminLoginFromIcannNetwork());
                throw new PageRedirectException(login);
            }
        }

        if (!getVisitState().getUser().isAdmin()) {
            Login login = (Login) logout();
            login.setAdminLoginError(getMessageUtil().getOnlyAdminErrorMessage());
            throw new PageRedirectException(login);
        }

    }

    protected AdminPage getRzmPage() {
        return (AdminPage) getPage();
    }

    public void resetStateIfneeded() {

    }


    public void handleNoObjectFoundException(NoObjectFoundException e) {
        getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
    }

    public boolean isLocalRequest(String address) {
        try {
            return InetAddress.getByName(address)
                .isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }// isLocalRequest

    public ICallback getCallback() {
        return null;
    }
}
