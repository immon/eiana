package org.iana.rzm.facade.admin.config.impl;

import org.iana.dns.DNSHost;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.admin.config.AdminConfigManager;
import org.iana.rzm.facade.admin.config.StatelessAdminConfigManager;
import org.iana.rzm.facade.admin.config.binded.*;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.user.UserVOManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedAdminConfigManagerImpl extends AbstractRZMStatefulService implements AdminConfigManager {

    StatelessAdminConfigManager statelessAdminConfigManager;

    public GuardedAdminConfigManagerImpl(UserVOManager userManager, StatelessAdminConfigManager statelessAdminConfigManager) {
        super(userManager);
        CheckTool.checkNull(statelessAdminConfigManager, "admin config manager");
        this.statelessAdminConfigManager = statelessAdminConfigManager;
    }

    public void setParameter(String name, String value) throws InfrastructureException {
        statelessAdminConfigManager.setParameter(name, value, getAuthenticatedUser());
    }

    public Pop3Config getPop3Config() throws InfrastructureException {
        return statelessAdminConfigManager.getPop3Config(getAuthenticatedUser());
    }

    public void setPop3Config(Pop3Config config) throws InfrastructureException {
        statelessAdminConfigManager.setPop3Config(config, getAuthenticatedUser());
    }

    public SmtpConfig getSmtpConfig() throws InfrastructureException {
        return statelessAdminConfigManager.getSmtpConfig(getAuthenticatedUser());
    }

    public void setSmtpConfig(SmtpConfig config) throws InfrastructureException {
        statelessAdminConfigManager.setSmtpConfig(config, getAuthenticatedUser());
    }

    public PgpConfig getPgpConfig() throws InfrastructureException {
        return statelessAdminConfigManager.getPgpConfig(getAuthenticatedUser());
    }

    public void setPgpConfig(PgpConfig config) throws InfrastructureException {
        statelessAdminConfigManager.setPgpConfig(config, getAuthenticatedUser());
    }

    public VerisignOrgConfig getVerisignOrgConfig() throws InfrastructureException {
        return statelessAdminConfigManager.getVerisignOrgConfig(getAuthenticatedUser());
    }

    public void setVersignOrgConfig(VerisignOrgConfig config) throws InfrastructureException {
        statelessAdminConfigManager.setVersignOrgConfig(config, getAuthenticatedUser());
    }

    public USDoCOrgConfig getUSDoCOrgConfig() throws InfrastructureException {
        return statelessAdminConfigManager.getUSDoCOrgConfig(getAuthenticatedUser());
    }

    public void setUSDoCOrgConfig(USDoCOrgConfig config) throws InfrastructureException {
        statelessAdminConfigManager.setUSDoCOrgConfig(config, getAuthenticatedUser());
    }

    public List<DNSHost> getRootNameservers() throws InfrastructureException {
        return statelessAdminConfigManager.getRootNameservers(getAuthenticatedUser());
    }

    public void setRootNameservers(List<DNSHost> nameservers) throws InfrastructureException {
        statelessAdminConfigManager.setRootNameservers(nameservers, getAuthenticatedUser());
    }
}
