package org.iana.rzm.facade.admin.config;

import org.iana.dns.DNSHost;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.config.binded.*;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminConfigManager {

    @Deprecated
    public void setParameter(String name, String value, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public Pop3Config getPop3Config(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public void setPop3Config(Pop3Config config, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public SmtpConfig getSmtpConfig(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public void setSmtpConfig(SmtpConfig config, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public PgpConfig getPgpConfig(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public void setPgpConfig(PgpConfig config, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public VerisignOrgConfig getVerisignOrgConfig(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public void setVersignOrgConfig(VerisignOrgConfig config, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public USDoCOrgConfig getUSDoCOrgConfig(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public void setUSDoCOrgConfig(USDoCOrgConfig config, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<DNSHost> getRootNameservers(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public void setRootNameservers(List<DNSHost> nameservers, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;
    
}
