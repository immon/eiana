package org.iana.rzm.web.util;

public class MessageUtil {
    
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
}
