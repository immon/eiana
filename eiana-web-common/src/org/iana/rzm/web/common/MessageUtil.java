package org.iana.rzm.web.common;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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

    public String getRadicalAlterationCheckMessage(String domainName) {
        return "You are trying to change all name servers for domain " + domainName + " We prefer that you " +
                "stagger the request in two requests such that any unexpected faults might be mitigated." ;
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

    public String getOnlyUserErrorMessage() {
        return "You tried to log in to the RZM User site using Admin Credential\n. Please use none admin Credential or use the RZM Admin website";
    }

    public String getMismatchSecureIdPinMessage() {
        return "New Pin does not match Confirm Pin.";
    }

    public String getSecureIdPinToLongMessage(int length) {
        return "Your pin is to long. It's should be " + length + " max";
    }

    public String getSecureIdPinToShortMessage(int length) {
        return "Your pin is to short. It's should be at least " + length + " long";
    }


    public String getPasswordMismatchMessage() {
        return "new Password does not match Confirm Password.";
    }

    public String getPasswordTheSameMessage() {
        return "New Password must be different from Current Password.";
    }

    public String getPasswordExpiredMessage(){
        return "Your Password has bean expired.";
    }

    public String getFirstLoginMessage(){
        return "This is your first login into the system. For security reason please change your password.";
    }

    public String getChangesSavedSuccessfullyMessage() {
        return "Your changes were saved Successfully.";

    }

    public String getAllNameServersFromSameASNumnerMessage(String value) {
        return "All name servers have the same AS number " + value  + ".";
    }

    public String getEmptyIpAddressListMessage(String hosts) {
        return "The folowing host does not seem to have have an ip address: " + "["+hosts+"].";
    }

    public String getMaximumPayloadSizeMessage(String receivedPayload, String expectedPayload) {
        return "The Response estimated size was " + receivedPayload +  " and it is greater than " + expectedPayload +  " bytes.";
    }

    public String getNameServerUnreachableByTCPMessage(String host) {
        return "The folowing host is not reachable by TCP: " + "["+host+"].";
    }

    public String getNameServerUnreachableMessage(String host) {
        return "The folowing host is not reachable: " + "["+host+"].";
    }

    public String getNameServerUnreachableByUdpMessage(String host) {
        return "The folowing host is not reachable by UDP: " + "["+host+"].";
    }

    public String getNotAuthoritativeNameServerMessage(String host, String domain) {
        return "The folowing host: [" + host + "] is not Authoritative for the domain: " + domain  + ".";
    }

    public String getNameServerCoherencyMessage(List<String> recivedNameServers, List<String> expectedNameServers, String host) {
        return "The NS RR-set returned by the authoritative name servers " + recivedNameServers.toString() +
                " are not the same as the supplied ns records " + expectedNameServers.toString() + " for Name server: " + host  + ".";
    }

    public String getNameServerIPAddressesNotEqualMessage(String host, List<String> recivedNameServers, List<String> expectedNameServers) {
        return "The A and AAAA records " +  recivedNameServers.toString() +
                " returned from the authoritative name server [" + host + "] are not the same as the supplied glue records " +
        expectedNameServers.toString() + ".";
    }

    public String NotUniqueIPAddressMessage(List<String> list) {
        return "The following name servers " + list.toString() + " share one or more IP Addresses.";
    }

    public String getNotEnoughNameServersMessage(String expected, String actual) {
        return "IANA Required a minimum of " +  expected + " name servers hosts you only have " + actual  + ".";
    }

    public String getReservedIPv4Message(String host, List<String> ips) {
        return "The following Name Server [" + host + "] is using a reserved IPV4 Address " + ips.toString() + ".";
    }

    public String getSerialNumberNotEqualMessage(List<String> serials) {
        return "The Authoritative name servers SOA serial number does not match: " + serials.toString() + "." ;
    }

    public String getWhoIsIOExceptionMessage(String host, List<String> ips) {
        return "We couldn't determine the autonomous system for Name Server [" +host+ "] IPV4 " + ips.toString() + ".";
    }
}
