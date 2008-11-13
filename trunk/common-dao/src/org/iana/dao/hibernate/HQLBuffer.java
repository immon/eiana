package org.iana.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HQLBuffer {

    private StringBuffer buf = new StringBuffer();

    private List<Object> params = new ArrayList<Object>();

    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isEmpty() {
        return buf.toString().trim().length() == 0;
    }

    public String getHQL() {
        return buf.toString();
    }

    public Object[] getParams() {
        return params.toArray();
    }

    public HQLBuffer op(String op, String fieldName, Object value) {
        buf.append(getName(fieldName)).append(" ").append(op).append(" ?");
        params.add(value);
        return sp();
    }

    public HQLBuffer op(String op, String fieldName) {
        buf.append(getName(fieldName)).append(" ").append(op);
        return sp();
    }

    public HQLBuffer in(String fieldName, Set<? extends Object> values) {
        if (values != null && !values.isEmpty()) {
            buf.append(getName(fieldName)).append(" ").append("in ").append('(');
            for (Object value : values) {
                buf.append("?,");
                params.add(value);
            }
            if (values.size() > 0) buf.deleteCharAt(buf.length() - 1);
            buf.append(')');
        }
        return sp();
    }

    private String getName(String fieldName) {
        return prefix != null ? prefix + "." + fieldName : fieldName;
    }

    public HQLBuffer append(String str) {
        buf.append(str).append(' ');
        return this;
    }

    public HQLBuffer append(HQLBuffer that) {
        if (that != null && that.buf.length() > 0) {
            this.buf.append('(').append(that.buf).append(')');
            this.params.addAll(that.params);
            return sp();
        }
        return this;
    }

    public HQLBuffer appendSimple(HQLBuffer that) {
        if (that != null) {
            this.buf.append(that.buf);
            this.params.addAll(that.params);
            return sp();
        }
        return this;
    }

    public HQLBuffer as(String alias) {
        buf.append("as").append(alias);
        return this;
    }

    public HQLBuffer nl() {
        buf.append('\n');
        return this;
    }

    public HQLBuffer sp() {
        buf.append(' ');
        return this;
    }

    public HQLBuffer order(String field, boolean asc) {
        if (!isEmpty()) buf.append(',');
        buf.append(field).append(' ').append(asc ? "asc" : "desc");
        return sp();
    }
}


