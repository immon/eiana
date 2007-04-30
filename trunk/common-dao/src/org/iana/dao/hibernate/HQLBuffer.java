package org.iana.dao.hibernate;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
class HQLBuffer {

    private StringBuffer buf = new StringBuffer();
    private List<Object> params = new ArrayList<Object>();

    public String getHQL() {
        return buf.toString();
    }

    public List<Object> getParams() {
        return params;
    }

    public HQLBuffer op(String op, String fieldName, Object value) {
        buf.append(fieldName).append(" ").append(op).append(" ?");
        params.add(value);
        return sp();
    }

    public HQLBuffer op(String op, String fieldName) {
        buf.append(fieldName).append(" ").append(op);
        return sp();
    }

    public HQLBuffer in(String fieldName, Set<Object> values) {
        buf.append(fieldName).append(" ").append("in ").append('(');
        for (Object value : values) {
            buf.append("?,");
            params.add(value);
        }
        if (values.size() > 0) buf.deleteCharAt(buf.length()-1);
        buf.append(')');
        return sp();
    }

    public HQLBuffer append(String str) {
        buf.append(str).append(' ');
        return this;
    }
    
    public HQLBuffer append(HQLBuffer that) {
        this.buf.append('(').append(that.buf).append(')');
        this.params.addAll(that.params);
        return sp();
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
}


