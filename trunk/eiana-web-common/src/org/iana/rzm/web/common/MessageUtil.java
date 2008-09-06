package org.iana.rzm.web.common;

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

    public String getAccessDeniedMessage() {
        return "You are not authorized to access this resource or preform this operation";
    }

    public String getNoCountryErrorMessage() {
        return "Please select a country";
    }

    public String getTicketNotAssignMessage() {
        return "Ticket number is not assign yet";
    }

    public String getOnlyAdminErrorMessage() {
        return "You must be admin to view this page";
    }

    public String getAdminLoginFromIcannNetwork() {
        return "You must login form an ICANN office network";
    }

    public String passwordChangeSuccessfull() {
        return "Your Password was change successfully";
    }

    public String getInvalidCountryCodeErrorMessage(String countryCode) {
        return "Invalid Country Code " + countryCode;
    }

    public String getInvalidDomainNameErrorMessage(String name, String reason) {
        return "Invalid Domain name " + name + " " + reason;
    }

    public String getNoRollErrorMessage() {
        return "User should have at least one role in a domain";
    }

    public String getMissingDomainErrorMessage() {
        return "Please specify value for domain";
    }

    public String getUserRollExsistForDomain(String role, String domain) {
        return "You are already in  role  " + role + " for the domain " + domain;
    }

    public String getNameServerChangeNotAllowedErrorMessage() {
        return "A name server change is not allowed for the domain at this time";
    }

    public String getStateChangeOKMessage() {
        return "State change successfully";
    }

    public String getStateChangeErrorMessage(String state) {
        return "This operation is not allowed for the current state " + state;
    }

    public String getInvalidStateErrorMessage(String stateName) {
        return "Invalid State " + stateName;
    }

    public String getNotificationSentOKMessage() {
        return "The Notification was sent successfully";
    }

    public String getUserNotFoundByUserNameErrorMessage(String userName) {
        return "Can't find user with user name: " + userName;
    }

    public String getRequestWithrowOKMessage() {
        return "The request was withdrawn Successfully";
    }

    public String getRequestNotFoundErrorMessage() {
        return "The request does not exsit ";
    }

    public String getTransactiomCannotBeWithdrawnErrorMessage() {
        return  "This request can't be withdrawn online. Please contact IANA for more information";
    }

    public String getPasswordChangeOkMessage() {
        return "Your Password was change successfully";
    }

    public String getPasswordMissmatchErrorMessage() {
        return "The new password and Confirm new password are not the same.";
    }

    public String getPasswordResetOkMessage() {
        return "Your Password was reset Successfully";
    }
}
