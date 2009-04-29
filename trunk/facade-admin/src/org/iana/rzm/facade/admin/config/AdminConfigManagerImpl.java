package org.iana.rzm.facade.admin.config;

import org.iana.config.Config;
import org.iana.config.ConfigDAO;
import org.iana.config.Parameter;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.SingleParameter;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.config.binded.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
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
                parameter = new SingleParameter(name, value, Config.DEFAULT_OWNER);
                configDAO.addParameter(parameter);
            } else if (parameter instanceof SingleParameter) {
                SingleParameter singleParameter = (SingleParameter) parameter;
                singleParameter.setValue(value);
            }
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

    public void setParameter(BindedParameter parameter) throws InfrastructureException {
        Map<String, String> values = parameter.getValuesMap();
        for (String paramName : values.keySet()) {
            setParameter(paramName, values.get(paramName));
        }
    }

    public Pop3Config getPop3Config() throws InfrastructureException {
        List<String> parameterNames = Pop3Config.getParameterNames();
        Map<String, String> values = getValuesMap(parameterNames);
        return new Pop3Config(values);
    }


    public SmtpConfig getSmtpConfig() throws InfrastructureException {
        List<String> parameterNames = SmtpConfig.getParameterNames();
        Map<String, String> values = getValuesMap(parameterNames);

        return new SmtpConfig(values);
    }

    public PgpConfig getPgpConfig() throws InfrastructureException {
        List<String> parameterNames = PgpConfig.getParameterNames();
        Map<String, String> values = getValuesMap(parameterNames);

        return new PgpConfig(values);
    }

    public VerisignOrgConfig getVerisignOrgConfig() throws InfrastructureException {
        List<String> parameterNames = VerisignOrgConfig.getParameterNames();
        Map<String, String> values = getValuesMap(parameterNames);

        return new VerisignOrgConfig(values);
    }

    public USDoCOrgConfig getUSDoCOrgConfig() throws InfrastructureException {
        List<String> parameterNames = USDoCOrgConfig.getParameterNames();
        Map<String, String> values = getValuesMap(parameterNames);

        return new USDoCOrgConfig(values);
    }

    private Map<String, String> getValuesMap(List<String> paramterNames) throws InfrastructureException {
        try {
            Map<String, String> values = new HashMap<String, String>();

            for (String paramName : paramterNames) {
                Parameter param = configDAO.getParameter(Config.DEFAULT_OWNER, paramName);
                if (param == null) {
                    values.put(paramName, null);
                } else {
                    values.put(paramName, param.getParameter());
                }
            }

            return values;
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

    public void setPop3Config(Pop3Config config) throws InfrastructureException {
        setParameter(config);
    }

    public void setSmtpConfig(SmtpConfig config) throws InfrastructureException {
        setParameter(config);
    }

    public void setPgpConfig(PgpConfig config) throws InfrastructureException {
        setParameter(config);
    }

    public void setVersignOrgConfig(VerisignOrgConfig config) throws InfrastructureException {
        setParameter(config);
    }

    public void setUSDoCOrgConfig(USDoCOrgConfig config) throws InfrastructureException {
        setParameter(config);
    }

}
