package org.iana.rzm.web.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.HomeService;
import org.apache.tapestry.engine.state.ApplicationStateManager;
import org.iana.rzm.web.Visit;
import org.iana.rzm.web.pages.admin.AdminHome;
import org.iana.rzm.web.pages.user.UserHome;

import java.io.IOException;

public class RzmHomeService extends HomeService {

    private static String ADMIN_HOME_PAGE = AdminHome.PAGE_NAME;
    private static String USER_HOME_PAGE = UserHome.PAGE_NAME;
    private ApplicationStateManager applicationStateManager;

    public String getPageName() {
        Visit visit = (Visit) applicationStateManager.get("visit");
        if (visit != null && visit.getUser() != null && visit.getUser().isAdmin()) {
            return ADMIN_HOME_PAGE;
        }
        return USER_HOME_PAGE;

    }

    public void service(IRequestCycle cycle) throws IOException {
        setPageName(getPageName());
        super.service(cycle);
    }

    public void setApplicationStateManager(ApplicationStateManager applicationStateManager) {
        this.applicationStateManager = applicationStateManager;
    }

}
