package org.iana.rzm.facade.admin.config;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.config.binded.*;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminConfigManager {

    public List<ConfigParameter> getParameters() throws InfrastructureException;

    public void setParameter(String name, String value) throws InfrastructureException;

    public Pop3Config getPop3Config() throws InfrastructureException;

    public void setPop3Config(Pop3Config config) throws InfrastructureException;

    public SmtpConfig getSmtpConfig() throws InfrastructureException;

    public void setSmtpConfig(SmtpConfig config) throws InfrastructureException;

    public PgpConfig getPgpConfig() throws InfrastructureException;

    public void setPgpConfig(PgpConfig config) throws InfrastructureException;

    public VerisignOrgConfig getVerisignOrgConfig() throws InfrastructureException;

    public void setVersignOrgConfig(VerisignOrgConfig config) throws InfrastructureException;

    public USDoCOrgConfig getUSDoCOrgConfig() throws InfrastructureException;

    public void setUSDoCOrgConfig(USDoCOrgConfig config) throws InfrastructureException;
    
}
