package org.iana.rzm.web.services;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 9, 2007
 * Time: 10:43:06 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TokenSynchronizer {

    public String getToken();
    public void setToken(String token);
    public boolean isResubmission();

}