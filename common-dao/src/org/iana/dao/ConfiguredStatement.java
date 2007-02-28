package org.iana.dao;

import org.iana.dao.statconfig.StatementConfigEntry;

import java.util.Map;
import java.util.HashMap;

/**
 * An abstract statement implementation based on a <code>StatementConfigEntry</code> being the metadata.
 * This class may serve as a base class for concrete <code>Update</code> and <code>Query</code> classes.
 *
 * @author Patrycja Wegrzynowicz
 */
abstract public class ConfiguredStatement implements Statement {

    final protected Map<String, Object> parameters = new HashMap<String, Object>();
    final protected StatementConfigEntry configEntry;

    protected ConfiguredStatement(StatementConfigEntry configEntry) {
        if (configEntry == null) throw new IllegalArgumentException("configEntry");
        this.configEntry = configEntry;
    }

    protected String getStatement() {
        return configEntry.getStatement();
    }

    public void setObject(String name, Object object) {
        if (name == null) throw new IllegalArgumentException("name");
        parameters.put(name, object);
    }

    abstract public Statement clone();
}
