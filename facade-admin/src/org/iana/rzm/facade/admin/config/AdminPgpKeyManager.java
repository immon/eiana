package org.iana.rzm.facade.admin.config;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.config.impl.PgpKeyInUseException;
import org.iana.rzm.facade.services.RZMStatefulService;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface AdminPgpKeyManager extends RZMStatefulService {

    PgpKeyVO get(String name) throws InfrastructureException;
                                              
    void create(PgpKeyVO pgpKeyVO) throws InfrastructureException;

    void update(PgpKeyVO pgpKeyVO) throws InfrastructureException;

    void delete(String name) throws InfrastructureException, PgpKeyInUseException;

    List<PgpKeyVO> getPgpKeys() throws InfrastructureException;
    
}
