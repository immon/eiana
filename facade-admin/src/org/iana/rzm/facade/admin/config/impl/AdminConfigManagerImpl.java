package org.iana.rzm.facade.admin.config.impl;

import org.iana.config.Config;
import org.iana.config.ConfigDAO;
import org.iana.config.Parameter;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.SingleParameter;
import org.iana.dns.DNSHost;
import org.iana.dns.RootServersProducer;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.config.AdminConfigManager;
import org.iana.rzm.facade.admin.config.binded.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class AdminConfigManagerImpl implements AdminConfigManager {

    protected ConfigParameterRetriever defaultParameterRetriever;

    protected ConfigDAO configDAO;

    protected RootServersProducer rootServers;

    public AdminConfigManagerImpl(ConfigDAO configDAO, ConfigParameterRetriever defaultValues, RootServersProducer rootServers) throws ConfigException {
        this.configDAO = configDAO;
        this.defaultParameterRetriever = defaultValues;
        this.rootServers = rootServers;
        initParameters();
    }

    private void initParameters() throws ConfigException {
        // init pop3 parameters
        initParameter(defaultParameterRetriever.getParameter(ConfigParameterNames.POP3_CLASS));
        // init smtp parameters
        initParameter(defaultParameterRetriever.getParameter(ConfigParameterNames.SMTP_CLASS));
        // init root name servers
        if (getRootNames() == null) {
            addRootNameservers(rootServers.getDefaultServers());
        }
    }

    private void initParameter(Map<String, String> values) throws ConfigException {
        if (values != null) {
            for (Map.Entry<String, String> parameter : values.entrySet()) {
                Parameter cfg = getParameter(parameter.getKey());
                if (cfg == null) {
                    cfg = new SingleParameter(parameter.getKey(), parameter.getValue());
                    configDAO.addParameter(cfg);
                }
            }
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
        // no default configured in spring (it's configured in templates.xml per email template)
        return new PgpConfig(values);
    }

    public VerisignOrgConfig getVerisignOrgConfig() throws InfrastructureException {
        List<String> parameterNames = VerisignOrgConfig.getParameterNames();
        Map<String, String> values = getValuesMap(parameterNames);
        if (values == null) values = initParameter(ConfigParameterNames.VERISIGN_EMAIL);
        // no default configured in spring (it's used only via database parameters)
        return new VerisignOrgConfig(values);
    }

    public USDoCOrgConfig getUSDoCOrgConfig() throws InfrastructureException {
        List<String> parameterNames = USDoCOrgConfig.getParameterNames();
        Map<String, String> values = getValuesMap(parameterNames);
        // no default configured in spring (it's used only via database parameters)
        return new USDoCOrgConfig(values);
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

    public List<DNSHost> getRootNameservers() throws InfrastructureException {
        try {
            return rootServers.getRootServers();
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

    public void setRootNameservers(List<DNSHost> nameservers) throws InfrastructureException {
        try {
            removeRootNameservers();
            addRootNameservers(nameservers);
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

    private void removeRootNameservers() throws ConfigException {
        Set<String> names = getRootNames();
        for (String name : names) {
            configDAO.removeParameter(Config.DEFAULT_OWNER, name);
        }
    }

    private void addRootNameservers(List<DNSHost> nameservers) throws ConfigException {
        List<Parameter> parameters = rootServers.toConfig(nameservers);
        for (Parameter param : parameters) {
            configDAO.addParameter(param);
        }
    }

    private Set<String> getRootNames() throws ConfigException {
        return configDAO.getSubConfigNames(Config.DEFAULT_OWNER, RootServersProducer.rootServerParamNames);
    }

    public void setParameter(String name, String value) throws InfrastructureException {
        try {
            Parameter parameter = getParameter(name);
            if (parameter == null) {
                SingleParameter singleParameter = new SingleParameter(name, value);
                configDAO.addParameter(singleParameter);
            } else if (parameter instanceof SingleParameter) {
                SingleParameter singleParameter = (SingleParameter) parameter;
                singleParameter.setValue(value);
            } else {
                throw new IllegalStateException("parameter of unexpected class: " + parameter.getClass().getName());
            }
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

    private Parameter getParameter(String name) throws ConfigException {
        return configDAO.getParameter(Config.DEFAULT_OWNER, name);
    }

    private void setParameter(BindedParameter parameter) throws InfrastructureException {
        setParameter(parameter.getValuesMap());
    }

    private void setParameter(Map<String, String> values) throws InfrastructureException {
        for (String paramName : values.keySet()) {
            setParameter(paramName, values.get(paramName));
        }
    }

    private Map<String, String> initParameter(String name) throws InfrastructureException {
        try {
            Map<String, String> ret = defaultParameterRetriever.getParameter(name);
            setParameter(ret);
            return ret;
        } catch (ConfigException e) {
            throw new InfrastructureException(e);
        }
    }

    private Map<String, String> getValuesMap(List<String> paramterNames) throws InfrastructureException {
        try {
            Map<String, String> values = new HashMap<String, String>();
            for (String paramName : paramterNames) {
                Parameter param = getParameter(paramName);
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


}
