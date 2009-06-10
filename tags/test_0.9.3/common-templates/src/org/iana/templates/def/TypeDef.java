package org.iana.templates.def;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class TypeDef implements Serializable {
    public static long serialVersionUID = 1L;

    private String name;
    private String regex;

    public TypeDef(String name) {
        this(name, null);
    }

    public TypeDef(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    public String getName() {
        return name;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("define ");
        sb.append(name);
        sb.append(" \"");
        sb.append(regex);
        sb.append("\"");
        return sb.toString();
    }
}
