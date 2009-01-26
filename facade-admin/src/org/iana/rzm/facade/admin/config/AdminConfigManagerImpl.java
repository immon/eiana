package org.iana.rzm.facade.admin.config;

import org.iana.config.ConfigDAO;
import org.iana.config.Parameter;
import org.iana.config.Config;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.SingleParameter;
import org.iana.rzm.common.exceptions.InfrastructureException;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AdminConfigManagerImpl implements AdminConfigManager {

    protected ConfigDAO configDAO;

    public AdminConfigManagerImpl(ConfigDAO configDAO) {
        this.configDAO = configDAO;
    }

    public List<ConfigParameter> getParameters() throws InfrastructureException {
        try {
            List<ConfigParameter> ret = new ArrayList<ConfigParameter>();
            List<Parameter> parameters = configDAO.getParameters();
            for (Parameter param : parameters) {
                ret.add(new ConfigParameter(param.getName(), param.getParameter()));
            }
            return ret;
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

    public void setParameter(String name, String value) throws InfrastructureException {
        try {
            Parameter parameter = configDAO.getParameter(Config.DEFAULT_OWNER, name);
            if (parameter == null) {
                parameter = new SingleParameter(name, value);
                configDAO.addParameter(parameter);
            } else if (parameter instanceof SingleParameter) {
                SingleParameter singleParameter = (SingleParameter) parameter;
                singleParameter.setValue(value);
            }
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

}
