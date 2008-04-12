package org.iana.rzm.facade.system.domain.converters;

import org.iana.dns.validator.*;
import org.iana.rzm.common.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.user.*;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

public class DomainFromVOConverter {

    private static Map<DomainVO.State, Domain.State> domainState = new HashMap<IDomainVO.State, Domain.State>();
    private static Map<DomainVO.Status, Domain.Status> domainStatus = new HashMap<IDomainVO.Status, Domain.Status>();
    private static Map<IDomainVO.Breakpoint, Domain.Breakpoint> domainBreakpoint = new HashMap<IDomainVO.Breakpoint, Domain.Breakpoint>();
    private static Map<RoleVO.Type, Role.Type> roleType = new HashMap<RoleVO.Type, Role.Type>();

    static {
        domainState.put(DomainVO.State.NO_ACTIVITY, Domain.State.NO_ACTIVITY);
        domainState.put(DomainVO.State.OPERATIONS_PENDING, Domain.State.OPERATIONS_PENDING);
        domainState.put(DomainVO.State.THIRD_PARTY_PENDING, Domain.State.THIRD_PARTY_PENDING);

        domainStatus.put(DomainVO.Status.ACTIVE, Domain.Status.ACTIVE);
        domainStatus.put(DomainVO.Status.CLOSED, Domain.Status.CLOSED);
        domainStatus.put(DomainVO.Status.NEW, Domain.Status.NEW);

        domainBreakpoint.put(IDomainVO.Breakpoint.AC_CHANGE_EXT_REVIEW, Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(IDomainVO.Breakpoint.ANY_CHANGE_EXT_REVIEW, Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(IDomainVO.Breakpoint.NS_CHANGE_EXT_REVIEW, Domain.Breakpoint.NS_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(IDomainVO.Breakpoint.SO_CHANGE_EXT_REVIEW, Domain.Breakpoint.SO_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(IDomainVO.Breakpoint.TC_CHANGE_EXT_REVIEW, Domain.Breakpoint.TC_CHANGE_EXT_REVIEW);

        roleType.put(SystemRoleVO.SystemType.AC, SystemRole.SystemType.AC);
        roleType.put(SystemRoleVO.SystemType.SO, SystemRole.SystemType.SO);
        roleType.put(SystemRoleVO.SystemType.TC, SystemRole.SystemType.TC);
    }

// ---------------------- Domain convert methods ----------------------

    public static Domain toDomain(IDomainVO fDomainVO) throws InvalidCountryCodeException, AccessDeniedException {
        if (fDomainVO == null) return null;

        Domain tDomain = new Domain("defaultName");
        toDomain(fDomainVO, tDomain);
        if (tDomain.getName().equals("defaultName"))
            throw new IllegalArgumentException("Name for domain not converted!");
        return tDomain;
    }

    private static void toDomain(IDomainVO fDomainVO, Domain tDomain) throws InvalidCountryCodeException, AccessDeniedException {
        try {
            tDomain.setName(fDomainVO.getName());
            tDomain.setObjId(fDomainVO.getObjId());
            tDomain.setAdminContact(toContact(fDomainVO.getAdminContact()));
            tDomain.setBreakpoints(toBreakpointSet(fDomainVO.getBreakpoints()));
            tDomain.setNameServers(toHostList(fDomainVO.getNameServers()));
            tDomain.setRegistryUrl(fDomainVO.getRegistryUrl());
            tDomain.setSpecialInstructions(fDomainVO.getSpecialInstructions());
//            tDomain.setState(toState(fDomainVO.getState()));
            tDomain.setStatus(toStatus(fDomainVO.getStatus()));
            tDomain.setSupportingOrg(toContact(fDomainVO.getSupportingOrg()));
            tDomain.setTechContact(toContact(fDomainVO.getTechContact()));
            if (fDomainVO.getWhoisServer() != null)
                tDomain.setWhoisServer(fDomainVO.getWhoisServer());

            TrackData trackData = new TrackData();
            trackData.setCreated(fDomainVO.getCreated());
            trackData.setCreatedBy(fDomainVO.getCreatedBy());
            trackData.setModified(fDomainVO.getModified());
            trackData.setModifiedBy(fDomainVO.getModifiedBy());
            tDomain.setTrackData(trackData);
            tDomain.setEnableEmails(fDomainVO.isEnableEmails());
            tDomain.setDescription(fDomainVO.getDescription());
            tDomain.setType(fDomainVO.getType());
            tDomain.setIanaCode(fDomainVO.getIanaCode());
        } catch (NameServerAlreadyExistsException e) {
            // imposible to occure because domain name servers set is converted from domainVO name servers set
            throw new IllegalArgumentException("Duplicated Name Servers", e);
        }
    }

    // ---------------------- State convert methods ----------------------

    public static Domain.State toState(IDomainVO.State fromStateVO) {
        if (fromStateVO == null) return null;

        Domain.State state = domainState.get(fromStateVO);
        CheckTool.checkNull(state, "Unknown domain state: " + fromStateVO);
        return state;
    }

// ---------------------- Status convert methods ----------------------

    public static Domain.Status toStatus(IDomainVO.Status fromStatusVO) {
        if (fromStatusVO == null) return null;

        Domain.Status status = domainStatus.get(fromStatusVO);
        CheckTool.checkNull(status, "Unknown domain status: " + fromStatusVO);
        return status;
    }

// ---------------------- Role convert methods ----------------------

    public static Role.Type toRoleType(RoleVO.Type fromRoleVOType) {
        if (fromRoleVOType == null) return null;

        Role.Type toRoleType = roleType.get(fromRoleVOType);
        CheckTool.checkNull(toRoleType, "Unknown role type: " + fromRoleVOType);
        return toRoleType;
    }

// ---------------------- Breakpoint convert methods ----------------------

    public static Set<Domain.Breakpoint> toBreakpointSet(Set<IDomainVO.Breakpoint> fromBreakpointVOSet) {
        if (fromBreakpointVOSet == null) return new HashSet<Domain.Breakpoint>();

        Set<Domain.Breakpoint> toBreakpointSet = new HashSet<Domain.Breakpoint>();
        for (IDomainVO.Breakpoint breakpointVO : fromBreakpointVOSet)
            toBreakpointSet.add(toBreakpoint(breakpointVO));
        return toBreakpointSet;
    }

    public static Domain.Breakpoint toBreakpoint(IDomainVO.Breakpoint fromBreakpointVO) {
        if (fromBreakpointVO == null) return null;

        Domain.Breakpoint breakpoint = domainBreakpoint.get(fromBreakpointVO);
        CheckTool.checkNull(breakpoint, "Unknown domain breakpoint: " + fromBreakpointVO);
        return breakpoint;
    }

// ---------------------- Host convert methods ----------------------

    public static List<Host> toHostList(List<HostVO> fromHostsVOList) {
        if (fromHostsVOList == null) return new ArrayList<Host>();

        List<Host> toHostList = new ArrayList<Host>();
        for (HostVO fromHostVO : fromHostsVOList)
            toHostList.add(toHost(fromHostVO));
        return toHostList;
    }

    public static Host toHost(HostVO fromHostVO) {
        if (fromHostVO == null) return null;

        Host tHost = new Host("defaultHost");
        toHost(fromHostVO, tHost);
        return tHost;
    }

    private static void toHost(HostVO fHostVO, Host tHost) {
        if (fHostVO == null) throw new IllegalArgumentException("null fromHostVO");
        if (tHost == null) throw new IllegalArgumentException("null toHost");

        tHost.setObjId(fHostVO.getObjId());
        tHost.setName(fHostVO.getName());
        try {
            tHost.setAddresses(toIPAddressSet(fHostVO.getAddresses()));
        } catch (InvalidIPAddressException e) {
            throw new IllegalArgumentException("Invalid IPAddress", e);
        }
        //todo what with numDelegations ??


        TrackData trackData = new TrackData();
        trackData.setCreated(fHostVO.getCreated());
        trackData.setCreatedBy(fHostVO.getCreatedBy());
        trackData.setModified(fHostVO.getModified());
        trackData.setModifiedBy(fHostVO.getModifiedBy());
        tHost.setTrackData(trackData);
    }

// ---------------------- IPAddress convert methods ----------------------

    public static Set<IPAddress> toIPAddressSet(Set<IPAddressVO> fromIPAddressVOSet) throws InvalidIPAddressException {
        if (fromIPAddressVOSet == null) return null;

        Set<IPAddress> tIPAddressSet = new HashSet<IPAddress>();
        for (IPAddressVO fromIPAddressVO : fromIPAddressVOSet)
            tIPAddressSet.add(toIPAddress(fromIPAddressVO));
        return tIPAddressSet;
    }

    public static IPAddress toIPAddress(IPAddressVO fromIPAddressVO) throws InvalidIPAddressException {
        if (fromIPAddressVO == null) return null;

        return IPAddress.createIPAddress(fromIPAddressVO.getAddress());
    }

    // ---------------------- Contact convert methods ----------------------
    public static List<Contact> toContactList(List<ContactVO> fromContactsVOList) throws InvalidCountryCodeException {
        if (fromContactsVOList == null) return null;

        List<Contact> tContactList = new ArrayList<Contact>();
        for (ContactVO fromContactVO : fromContactsVOList)
            tContactList.add(toContact(fromContactVO));
        return tContactList;
    }

    public static Contact toContact(ContactVO fromContactVO) throws InvalidCountryCodeException {
        if (fromContactVO == null) return null;

        Contact tContact = new Contact();
        toContact(fromContactVO, tContact);
        return tContact;
    }

    private static void toContact(ContactVO fContactVO, Contact tContact) throws InvalidCountryCodeException {
        if (fContactVO == null) throw new IllegalArgumentException("null fromContactVO");
        if (tContact == null) throw new IllegalArgumentException("null toContact");

        tContact.setName(fContactVO.getName());
        tContact.setOrganization(fContactVO.getOrganization());
        tContact.setJobTitle(fContactVO.getJobTitle());
        tContact.setAddress(toAddress(fContactVO.getAddress()));
        tContact.setFaxNumber(fContactVO.getFaxNumber());
        tContact.setAltFaxNumber(fContactVO.getAltFaxNumber());
        tContact.setPhoneNumber(fContactVO.getPhoneNumber());
        tContact.setAltPhoneNumber(fContactVO.getAltPhoneNumber());
        tContact.setEmail(fContactVO.getEmail());
        tContact.setPrivateEmail(fContactVO.getPrivateEmail());
        tContact.setRole(fContactVO.isRole());

        TrackData trackData = new TrackData();
        trackData.setCreated(fContactVO.getCreated());
        trackData.setCreatedBy(fContactVO.getCreatedBy());
        trackData.setModified(fContactVO.getModified());
        trackData.setModifiedBy(fContactVO.getModifiedBy());
        tContact.setTrackData(trackData);
    }

// ---------------------- Address convert methods ----------------------

    public static List<Address> toAddressList(List<AddressVO> fAddressVOList) throws InvalidCountryCodeException {
        if (fAddressVOList == null) return null;

        List<Address> tAddressList = new ArrayList<Address>();
        for (AddressVO fAddressVO : fAddressVOList)
            tAddressList.add(toAddress(fAddressVO));
        return tAddressList;
    }

    public static Address toAddress(AddressVO fAddressVO) throws InvalidCountryCodeException {
        if (fAddressVO == null) return null;

        Address tAddress = new Address();
        toAddress(fAddressVO, tAddress);
        return tAddress;
    }

    private static void toAddress(AddressVO fAddressVO, Address tAddress) throws InvalidCountryCodeException {
        if (fAddressVO == null) throw new IllegalArgumentException("null fromAddressVO");
        if (tAddress == null) throw new IllegalArgumentException("null toAddress");

        tAddress.setTextAddress(fAddressVO.getTextAddress());
        tAddress.setCountryCode(fAddressVO.getCountryCode());
    }
}

