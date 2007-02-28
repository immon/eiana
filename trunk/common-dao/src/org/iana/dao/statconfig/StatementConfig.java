package org.iana.dao.statconfig;

import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.env.Environment;

import java.util.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * A config entry for a single statement.
 *
 * @author Patrycja Wegrzynowicz
 */
public class StatementConfig {

    private Map<String, StatementConfigEntry> updates = new HashMap<String, StatementConfigEntry>();
    private Map<String, StatementConfigEntry> queries = new HashMap<String, StatementConfigEntry>();

    public void addQuery(StatementConfigEntry entry) {
        if (entry == null) throw new IllegalArgumentException("queryConfigEntry");
        queries.put(entry.getName(), entry);
    }

    public void addUpdate(StatementConfigEntry entry) {
        if (entry == null) throw new IllegalArgumentException("updateConfigEntry");
        updates.put(entry.getName(), entry);
    }

    public void setQueries(Collection<StatementConfigEntry> queries) {
        if (queries == null) throw new IllegalArgumentException("queries");
        for (StatementConfigEntry query : queries) addQuery(query);
    }

    public void setUpdates(Collection<StatementConfigEntry> updates) {
        if (updates == null) throw new IllegalArgumentException("updates");
        for (StatementConfigEntry update : updates) addQuery(update);
    }

    final private static Map<String, StatementConfig> instances = new HashMap<String, StatementConfig>();

    public static synchronized StatementConfig getInstance(String xmlConfig) {
        try {
            if (!instances.containsKey(xmlConfig)) {
                DynaXMLParser parser = new DynaXMLParser();
                Environment environment = DPConfig.getEnvironment("statconfig.properties");
                Reader input = new InputStreamReader(parser.getClass().getClassLoader().getResourceAsStream(xmlConfig), "UTF-8");
                StatementConfig config = (StatementConfig) parser.fromXML(input, environment);
                instances.put(xmlConfig, config);
            }
            return instances.get(xmlConfig);
        } catch (UnsupportedEncodingException e) {
            throw new ConfigInitializationFailedException(e, xmlConfig);
        } catch (DynaXMLException e) {
            throw new ConfigInitializationFailedException(e, xmlConfig);
        }
    }
}
