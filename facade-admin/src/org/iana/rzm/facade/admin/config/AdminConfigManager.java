package org.iana.rzm.facade.admin.config;

import org.iana.rzm.common.exceptions.InfrastructureException;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminConfigManager {

    public List<ConfigParameter> getParameters() throws InfrastructureException;

    public void setParameter(String name, String value) throws InfrastructureException;

}
