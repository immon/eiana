package org.iana.rzm.web.util;

import java.io.*;
import java.util.*;

public class MessageUtil implements Serializable {
    
    public String getSessionRestorefailedMessage(){
        return "Can't restore session. Please restart session using the link supplied";
    }

    public String getTransactionNotFoundMessage(String reasion){
        return "Can't find Request with " + reasion;
    }

    public String getRequestFinderValidationErrorMessage(String value) {
        return "Invalid value for request: " + value;
    }

    public String getDomainModificationErrorMessage(String domainName) {
        return "You can't modify this Domain " + domainName + " At This time";
    }

    public String getRecoverUserNameMessage(){
        return "We couldn't retrieve your user name based on the information provided. " +
               "Please contact <a href='mailto:root-mgmt@iana.org'>IANA Root Management</a> for assistance.";
    }

    public String getSessionTimeoutMessage(){
        return "Your Session has timed out. Please login to continue";
    }

    public String getAllNameServersChangeMessage() {
        return "Changing all Name Servers in one request is not allowed.";
    }

    public String getSharedNameServersCollisionMessage(Set<String> nameServers) {
        StringBuilder builder = new StringBuilder();
        builder.append("Changing a shared name server which is not part of your current name server list is not allowed.");
        return builder.toString();

    }
}
