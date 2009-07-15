package org.iana.rzm.facade.admin.config.impl;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.admin.config.AdminPgpKeyManager;
import org.iana.rzm.facade.admin.config.PgpKeyVO;
import org.iana.rzm.facade.admin.config.StatelessAdminPgpKeyManager;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.user.UserVOManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class AdminPgpKeyManagerImpl extends AbstractRZMStatefulService implements AdminPgpKeyManager {

    StatelessAdminPgpKeyManager statelessAdminPgpKeyManager;

    public AdminPgpKeyManagerImpl(UserVOManager userManager, StatelessAdminPgpKeyManager statelessAdminPgpKeyManager) {
        super(userManager);
        CheckTool.checkNull(statelessAdminPgpKeyManager, "admin pgp key manager");
        this.statelessAdminPgpKeyManager = statelessAdminPgpKeyManager;
    }

    public PgpKeyVO get(String name) throws InfrastructureException {
        return statelessAdminPgpKeyManager.get(name, getAuthenticatedUser());
    }

    public void create(PgpKeyVO pgpKeyVO) throws InfrastructureException {
        statelessAdminPgpKeyManager.create(pgpKeyVO, getAuthenticatedUser());
    }

    public void update(PgpKeyVO pgpKeyVO) throws InfrastructureException {
        statelessAdminPgpKeyManager.update(pgpKeyVO, getAuthenticatedUser());
    }

    public void delete(String name) throws InfrastructureException, PgpKeyInUseException {
        statelessAdminPgpKeyManager.delete(name, getAuthenticatedUser());
    }

    public List<PgpKeyVO> getPgpKeys() throws InfrastructureException {
        return statelessAdminPgpKeyManager.getPgpKeys();
    }

}
