package org.iana.dao.statconfig;

/**
 * @author Patrycja Wegrzynowicz
 */
public class StatementConfigEntry {

    private String name;
    private String statement;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}
