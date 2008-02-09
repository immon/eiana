package org.iana.rzm.web.services;

import org.apache.tapestry.engine.state.ApplicationStateManager;
import org.iana.rzm.web.Visit;

public class ApplicationLifecycleImpl implements ApplicationLifecycle {

   private boolean discardSession;

    private ApplicationStateManager stateManager;

    public void setStateManager(ApplicationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void logout() {
        discardSession = true;

        if (stateManager.exists("visit")) {
            Visit visit = (Visit) stateManager.get("visit");
            visit.clearCache();
        }
    }

    public boolean getDiscardSession() {
        return discardSession;
    }
}
