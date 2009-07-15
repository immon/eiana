package org.iana.rzm.facade.admin.config;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.config.impl.PgpKeyInUseException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminPgpKeyManager {

    PgpKeyVO get(String name, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    void create(PgpKeyVO pgpKeyVO, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    void update(PgpKeyVO pgpKeyVO , AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    void delete(String name, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, PgpKeyInUseException;

    List<PgpKeyVO> getPgpKeys() throws InfrastructureException;
}
