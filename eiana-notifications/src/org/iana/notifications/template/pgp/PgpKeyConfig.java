package org.iana.notifications.template.pgp;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface PgpKeyConfig {

    PgpKey getPgpKey(String name);

    void create(PgpKey pgpKey);

    void update(PgpKey pgpKey);

    void delete(String name);

    List<PgpKey> getPgpKeys();
}
