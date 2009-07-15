package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.config.PgpKeyVO;
import org.iana.rzm.facade.admin.config.StatelessAdminPgpKeyManager;
import org.iana.rzm.facade.admin.config.impl.PgpKeyInUseException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test
public class AdminPgpKeyManagerTest extends RollbackableSpringContextTest {

    private static final String DEFAULT_KEY = "defaultKey";

    protected StatelessAdminPgpKeyManager statelessAdminPgpKeyManager;
    protected UserManager userManager;

    private AuthenticatedUser authUser;

    public AdminPgpKeyManagerTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    @BeforeClass
    protected void init() throws Exception {

        RZMUser root = new RZMUser();
        root.setLoginName("root");
        AdminRole adminRole = new AdminRole(AdminRole.AdminType.ROOT);
        root.addRole(adminRole);
        userManager.create(root);
        authUser = new AuthenticatedUser(root.getObjId(), root.getLoginName(), true, true);

    }

    @Test
    public void testGetPgpKeys() throws Exception {
        List<PgpKeyVO> pgpKeys = statelessAdminPgpKeyManager.getPgpKeys();
        assert pgpKeys != null;
        assert !pgpKeys.isEmpty();
    }

    @Test(expectedExceptions = PgpKeyInUseException.class)
    public void deleteUsedPgpKey() throws Exception {

        PgpKeyVO defaultKey = statelessAdminPgpKeyManager.get(DEFAULT_KEY, authUser);

        assert defaultKey != null;
        assert defaultKey.getName().equals(DEFAULT_KEY);

        statelessAdminPgpKeyManager.delete(DEFAULT_KEY, authUser);
    }

    protected void cleanUp() throws Exception {
    }

}
