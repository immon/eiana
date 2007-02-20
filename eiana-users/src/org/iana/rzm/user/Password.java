package org.iana.rzm.user;

public interface Password {

    public void setPassword(String password);

    public boolean isValid(String password);
}
