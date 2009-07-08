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
        return "You can't modify this domain " + domainName + " at this time";
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
        return "Ticket number is not assigned yet";
    }

    public String getOnlyAdminErrorMessage() {
        return "You must have administrative rights to view this page";
    }

    public String getAdminLoginFromIcannNetwork() {
        return "You must login form an ICANN office network";
    }

    public String passwordChangeSuccessfull() {
        return "Your Password was changed successfully";
    }

    public String getInvalidCountryCodeErrorMessage(String countryCode) {
        return "Invalid Country Code " + countryCode;
    }

    public String getInvalidDomainNameErrorMessage(String name, String reason) {
        return "Invalid domain name " + name + " " + reason;
    }

    public String getNoRollErrorMessage() {
        return "User should have at least one role in a domain";
    }

    public String getMissingDomainErrorMessage() {
        return "Please specify value for domain";
    }

    public String getUserRollExsistForDomain(String role, String domain) {
        return "You are already a " + role + " for domain " + domain;
    }

    public String getNameServerChangeNotAllowedErrorMessage() {
        return "A name server change is not allowed for the domain at this time";
    }

    public String getStateChangeOKMessage() {
        return "State changed successfully";
    }

    public String getStateChangeErrorMessage(String state) {
        return "This operation is not allowed for the current state " + state;
    }

    public String getInvalidStateErrorMessage(String stateName) {
        return "Invalid state " + stateName;
    }

    public String getNotificationSentOKMessage() {
        return "The notification was sent successfully";
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
        return  "This request can't be withdrawn online. Please contact <a href='mailto:root-mgmt@iana.org'>IANA Root Management</a> for assistance.";
    }

    public String getPasswordChangeOkMessage() {
        return "Your password was changed successfully";
    }

    public String getPasswordMissmatchErrorMessage() {
        return "The values for the new password and confirmed new password are not the same.";
    }

    public String getPasswordResetOkMessage() {
        return "Your password was reset Successfully";
    }

    public String getOnlyUserErrorMessage() {
        return "You tried to log in to the RZM User site using Admin Credential\n. Please use non-admin credential or use the RZM Admin website";
    }

    public String getMismatchSecureIdPinMessage() {
        return "New pin does not match confirmed pin.";
    }

    public String getSecureIdPinToLongMessage(int length) {
        return "Your pin is tot long. The maximum value should be " + length ;
    }

    public String getSecureIdPinToShortMessage(int length) {
        return "Your pin is too short. The minmum length should be at least " + length + " long";
    }

    public String getPasswordTheSameMessage() {
        return "New password must be different from current password.";
    }

    public String getPasswordExpiredMessage(){
        return "Your password has expired.";
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
        return "The following host does not seem to have an IP address: " + "["+hosts+"].";
    }

    public String getMaximumPayloadSizeMessage(String receivedPayload, String expectedPayload) {
        return "The response estimated size was " + receivedPayload +  " and it is greater than " + expectedPayload +  " bytes.";
    }

    public String getNameServerUnreachableByTCPMessage(String host) {
        return "The following host is not reachable by TCP: " + "["+host+"].";
    }

    public String getNameServerUnreachableMessage(String host) {
        return "The following host is not reachable: " + "["+host+"].";
    }

    public String getNameServerUnreachableByUdpMessage(String host) {
        return "The following host is not reachable by UDP: " + "["+host+"].";
    }

    public String getNotAuthoritativeNameServerMessage(String host, String domain) {
        return "The following host: [" + host + "] is not authoritative for domain: " + domain  + ".";
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
        return "IANA Requires a minimum of " +  expected + " name servers. You only have " + actual  + " name server.";
    }

    public String getReservedIPv4Message(String host, List<String> ips) {
        return "The following name server [" + host + "] is using a reserved IPV4 address " + ips.toString() + ".";
    }

    public String getSerialNumberNotEqualMessage(List<String> serials) {
        return "The authoritative name servers SOA serial numbers do not match: " + serials.toString() + "." ;
    }

    public String getWhoIsIOExceptionMessage(String host, List<String> ips) {
        return "We couldn't determine the autonomous system for name server [" +host+ "] IPV4 " + ips.toString() + ".";
    }

    public String getDoaminDoesNotExsitMessage(String domain) {
        return "Can't find domain with name "  + domain;
    }

    public String missingrequiredFieldMessage(String fieldName) {
        return "Please specify value for " + fieldName;
    }

    public String getTransactionExistMessage(String domainName) {
        return "Can't create a new request. There are requests pending for domain " + domainName ;
    }

    public String getNoDomainModificationMessage(String domainName) {
        return "You are trying to submit a request to change domain " + domainName + " with no changes";
    }
}
