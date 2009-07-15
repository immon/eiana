package org.iana.rzm.facade.admin.config.impl;

import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.def.TemplateDefConfig;
import org.iana.notifications.template.pgp.PgpKey;
import org.iana.notifications.template.pgp.PgpKeyConfig;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.admin.config.PgpKeyVO;
import org.iana.rzm.facade.admin.config.StatelessAdminPgpKeyManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.user.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class StatelessAdminPgpKeyManagerImpl extends AbstractRZMStatelessService implements StatelessAdminPgpKeyManager {

    private PgpKeyConfig pgpKeyConfig;
    private TemplateDefConfig templateDefConfig;

    public StatelessAdminPgpKeyManagerImpl(UserManager userManager, PgpKeyConfig pgpKeyConfig, TemplateDefConfig templateDefConfig) {
        super(userManager);
        CheckTool.checkNull(pgpKeyConfig, "pgp key config");
        CheckTool.checkNull(templateDefConfig, "template def config");
        this.pgpKeyConfig = pgpKeyConfig;
        this.templateDefConfig = templateDefConfig;
    }


    public PgpKeyVO get(String name, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isRoot(authUser);
        PgpKey pgpKey = pgpKeyConfig.getPgpKey(name);
        return (pgpKey == null)? null : toPgpKeyVO(pgpKey);
    }

    public void create(PgpKeyVO pgpKeyVO, AuthenticatedUser authUser) throws InfrastructureException {
        isRoot(authUser);
        pgpKeyConfig.create(toPgpKey(pgpKeyVO));
    }

    public void update(PgpKeyVO pgpKeyVO, AuthenticatedUser authUser) throws InfrastructureException {
        isRoot(authUser);
        pgpKeyConfig.update(toPgpKey(pgpKeyVO));
    }

    public void delete(String name, AuthenticatedUser authUser) throws InfrastructureException, PgpKeyInUseException {
        isRoot(authUser);
        if (isPgpKeyInUse(name))
            throw new PgpKeyInUseException(name);

        pgpKeyConfig.delete(name);
    }

    public List<PgpKeyVO> getPgpKeys() throws InfrastructureException {
        return toPgpKeyVOs(pgpKeyConfig.getPgpKeys());
    }

    private boolean isPgpKeyInUse(String name) {
        List<TemplateDef> templateDefs = templateDefConfig.getTemplateDefs();
        for (TemplateDef templateDef : templateDefs ) {
            if (templateDef.getKeyName().equals(name))
                return true;
        }

        return false;
    }

    private PgpKey toPgpKey(PgpKeyVO src) {
        PgpKey ret = new PgpKey();
        ret.setName(src.getName());
        ret.setArmouredKey(src.getArmouredKey());
        ret.setPassphrase(src.getPassphrase());
        return ret;
    }

    private List<PgpKeyVO> toPgpKeyVOs(List<PgpKey> src) {
        List<PgpKeyVO> ret = new ArrayList<PgpKeyVO>();
        if (src != null) {
            for (PgpKey tmp : src) {
                ret.add(toPgpKeyVO(tmp));
            }
        }
        return ret;
    }

    private PgpKeyVO toPgpKeyVO(PgpKey src) {
        PgpKeyVO ret = new PgpKeyVO();
        ret.setName(src.getName());
        ret.setArmouredKey(src.getArmouredKey());
        ret.setPassphrase(src.getPassphrase());
        return ret;
    }
}
