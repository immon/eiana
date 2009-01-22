package org.iana.rzm.web.common;


public class Global {
    private final boolean DEBUG_ENABLED = Boolean.getBoolean("org.iana.web.debug-enabled");

    public boolean isDebugEnabled() {
        return DEBUG_ENABLED;
    }
}
