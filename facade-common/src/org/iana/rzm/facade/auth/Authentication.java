package org.iana.rzm.facade.auth;

import java.io.*;

/**
 * <p>Available authentication types.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public enum Authentication implements Serializable {
    PASSWORD,
    SECURID
    /* PGP? */
}
