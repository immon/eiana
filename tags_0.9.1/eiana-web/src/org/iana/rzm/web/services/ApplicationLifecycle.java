package org.iana.rzm.web.services;

public interface ApplicationLifecycle {
    /**
     * Logs the user out of the systems; sets a flag that causes the session to be discarded at the
     * end of the request.
     */
    void logout();

    /**
     * If true, then the session (if it exists) should be discarded at the end of the request.
     */
    boolean getDiscardSession();
}
