package org.iana.rzm.facade.system.trans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionActionVO implements Serializable {

    public static final String MODIFY_SO = "modify supporting organization";
    public static final String MODIFY_AC = "modify administrative contact";
    public static final String MODIFY_TC = "modify technical contact";
    public static final String MODIFY_REGISTRATION_URL = "modify registration url";
    public static final String MODIFY_WHOIS_SERVER = "modify whois server";
    public static final String MODIFY_NAME_SERVERS = "modify name servers";

    private String name;
    private List<ChangeVO> change;

    public TransactionActionVO() {
    }

    public TransactionActionVO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChangeVO> getChange() {
        return change;
    }

    public void setChange(List<ChangeVO> change) {
        this.change = change;
    }

    public void addChange(ChangeVO change) {
        if (this.change == null) this.change = new ArrayList<ChangeVO>();
        this.change.add(change);
    }

    public void addChange(List<ChangeVO> changes) {
        if (this.change == null) this.change = new ArrayList<ChangeVO>();
        this.change.addAll(changes);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionActionVO that = (TransactionActionVO) o;

        if (change != null ? !change.equals(that.change) : that.change != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (change != null ? change.hashCode() : 0);
        return result;
    }
}
