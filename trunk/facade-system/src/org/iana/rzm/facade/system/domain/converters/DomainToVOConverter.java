package org.iana.rzm.facade.system.domain.converters;

import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.user.*;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

public class DomainToVOConverter {

    private static Map<Domain.State, DomainVO.State> domainState = new HashMap<Domain.State, DomainVO.State>();
    private static Map<Domain.Status, DomainVO.Status> domainStatus = new HashMap<Domain.Status, IDomainVO.Status>();
    private static Map<Domain.Breakpoint, IDomainVO.Breakpoint> domainBreakpoint = new HashMap<Domain.Breakpoint, IDomainVO.Breakpoint>();
    private static Map<Role.Type, RoleVO.Type> roleType = new HashMap<Role.Type, RoleVO.Type>();

    static {
        domainState.put(Domain.State.NO_ACTIVITY, DomainVO.State.NO_ACTIVITY);
        domainState.put(Domain.State.OPERATIONS_PENDING, DomainVO.State.OPERATIONS_PENDING);
        domainState.put(Domain.State.THIRD_PARTY_PENDING, DomainVO.State.THIRD_PARTY_PENDING);

        domainStatus.put(Domain.Status.ACTIVE, DomainVO.Status.ACTIVE);
        domainStatus.put(Domain.Status.CLOSED, DomainVO.Status.CLOSED);
        domainStatus.put(Domain.Status.NEW, DomainVO.Status.NEW);

        domainBreakpoint.put(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW, IDomainVO.Breakpoint.AC_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW, IDomainVO.Breakpoint.ANY_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(Domain.Breakpoint.NS_CHANGE_EXT_REVIEW, IDomainVO.Breakpoint.NS_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(Domain.Breakpoint.SO_CHANGE_EXT_REVIEW, IDomainVO.Breakpoint.SO_CHANGE_EXT_REVIEW);
        domainBreakpoint.put(Domain.Breakpoint.TC_CHANGE_EXT_REVIEW, IDomainVO.Breakpoint.TC_CHANGE_EXT_REVIEW);

        roleType.put(SystemRole.SystemType.AC, SystemRoleVO.SystemType.AC);
        roleType.put(SystemRole.SystemType.SO, SystemRoleVO.SystemType.SO);
        roleType.put(SystemRole.SystemType.TC, SystemRoleVO.SystemType.TC);
    }

    // ---------------------- Domain convert methods ----------------------
    public static DomainVO toDomainVO(Domain fromDomain) throws InvalidCountryCodeException, InvalidEmailException {
        if (fromDomain == null) return null;

        DomainVO toDomainVO = new DomainVO();
        toDomainVO(fromDomain, toDomainVO);
        return toDomainVO;
    }

    private static void toDomainVO(Domain fromDomain, DomainVO toDomainVO) throws InvalidCountryCodeException, InvalidEmailException {
        if (fromDomain == null) throw new IllegalArgumentException("null fromDomain");
        if (toDomainVO == null) throw new IllegalArgumentException("null toDomainVO");


        toSimpleDomainVO(fromDomain, toDomainVO);
        toDomainVO.setSupportingOrg(toContactVO(fromDomain.getSupportingOrg()));

        toDomainVO.setAdminContact(toContactVO(fromDomain.getAdminContact()));

        toDomainVO.setTechContact(toContactVO(fromDomain.getTechContact()));

        toDomainVO.setNameServers(toHostVOList(fromDomain.getNameServers()));

        toDomainVO.setRegistryUrl(fromDomain.getRegistryUrl());

        if (fromDomain.getWhoisServer() != null) toDomainVO.setWhoisServer(fromDomain.getWhoisServer());

        toDomainVO.setBreakpoints(toBreakpointVOSet(fromDomain.getBreakpoints()));

        toDomainVO.setSpecialInstructions(fromDomain.getSpecialInstructions());

        toDomainVO.setStatus(toStatusVO(fromDomain.getStatus()));
        toDomainVO.setState(toStateVO(fromDomain.getState()));

        toDomainVO.setEnableEmails(fromDomain.isEnableEmails());

        toDomainVO.setDescription(fromDomain.getDescription());
        toDomainVO.setType(fromDomain.getType());
        toDomainVO.setIanaCode(fromDomain.getIanaCode());
    }

    public static SimpleDomainVO toSimpleDomainVO(Domain fromDomain) {
        if (fromDomain == null) return null;

        SimpleDomainVO simpleDomainVO = new SimpleDomainVO();
        toSimpleDomainVO(fromDomain, simpleDomainVO);
        return simpleDomainVO;
    }

    private static void toSimpleDomainVO(Domain fromDomain, SimpleDomainVO toSimpleDomainVO) {
        if (fromDomain == null) throw new IllegalArgumentException("null fromDomain");
        if (toSimpleDomainVO == null) throw new IllegalArgumentException("null toSimpleDomainVO");

        toSimpleDomainVO.setObjId(fromDomain.getObjId());
        toSimpleDomainVO.setName(fromDomain.getName());
        toSimpleDomainVO.setSpecialInstructions(fromDomain.getSpecialInstructions());
        toSimpleDomainVO.setDescription(fromDomain.getDescription());

        if (fromDomain.getTrackData() != null) {
            toSimpleDomainVO.setCreated(fromDomain.getCreated());
            toSimpleDomainVO.setCreatedBy(fromDomain.getCreatedBy());
            toSimpleDomainVO.setModified(fromDomain.getModified());
            toSimpleDomainVO.setModifiedBy(fromDomain.getModifiedBy());
        }
    }

// ---------------------- State convert methods ----------------------

    public static IDomainVO.State toStateVO(Domain.State fromState) {
        if (fromState == null) return null;

        IDomainVO.State stateVO = domainState.get(fromState);
        CheckTool.checkNull(stateVO, "Unknown domain state: " + fromState);
        return stateVO;
    }

// ---------------------- Status convert methods ----------------------

    public static IDomainVO.Status toStatusVO(Domain.Status fromStatus) {
        if (fromStatus == null) return null;

        IDomainVO.Status statusVO = domainStatus.get(fromStatus);
        CheckTool.checkNull(statusVO, "Unknown domain status: " + fromStatus);
        return statusVO;
    }

// ---------------------- Role convert methods ----------------------

    public static RoleVO.Type toRoleTypeVO(Role.Type fromRoleType) {
        if (fromRoleType == null) return null;

        RoleVO.Type roleTypeVO = roleType.get(fromRoleType);
        CheckTool.checkNull(roleTypeVO, "Unknown role type: " + fromRoleType);
        return roleTypeVO;
    }

// ---------------------- Breakpoint convert methods ----------------------

    public static Set<IDomainVO.Breakpoint> toBreakpointVOSet(Set<Domain.Breakpoint> fromBreakpointSet) {
        if (fromBreakpointSet == null) return null;

        Set<IDomainVO.Breakpoint> toBreakpointVOSet = new HashSet<IDomainVO.Breakpoint>();
        for (Domain.Breakpoint breakpoint : fromBreakpointSet)
            toBreakpointVOSet.add(toBreakpointVO(breakpoint));
        return toBreakpointVOSet;
    }

    public static IDomainVO.Breakpoint toBreakpointVO(Domain.Breakpoint fromBreakpoint) {
        if (fromBreakpoint == null) return null;

        IDomainVO.Breakpoint breakpointVO = domainBreakpoint.get(fromBreakpoint);
        CheckTool.checkNull(breakpointVO, "Unknown domain breakpoint: " + fromBreakpoint);
        return breakpointVO;
    }

// ---------------------- Host convert methods ----------------------

    public static List<HostVO> toHostVOList(List<Host> fromHostsList) {
        if (fromHostsList == null) return null;

        List<HostVO> toHostVOList = new ArrayList<HostVO>();
        for (Host fromHost : fromHostsList)
            toHostVOList.add(toHostVO(fromHost));
        return toHostVOList;
    }

    public static HostVO toHostVO(Host fromHost) {
        if (fromHost == null) return null;

        HostVO toHostVO = new HostVO();
        toHostVO(fromHost, toHostVO);
        return toHostVO;
    }

    private static void toHostVO(Host fromHost, HostVO toHostVO) {
        if (fromHost == null) throw new IllegalArgumentException("null fromHost");
        if (toHostVO == null) throw new IllegalArgumentException("null toHostVO");

        toHostVO.setObjId(fromHost.getObjId());
        toHostVO.setAddresses(toIPAddressVOSet(fromHost.getAddresses()));
        toHostVO.setName(fromHost.getName());
        toHostVO.setShared(fromHost.isShared());

        if (fromHost.getTrackData() != null) {
            toHostVO.setCreated(fromHost.getCreated());
            toHostVO.setCreatedBy(fromHost.getCreatedBy());
            toHostVO.setModified(fromHost.getModified());
            toHostVO.setModifiedBy(fromHost.getModifiedBy());
        }
    }

// ---------------------- IPAddress convert methods ----------------------

    public static Set<IPAddressVO> toIPAddressVOSet(Set<IPAddress> fromIPAddressSet) {
        if (fromIPAddressSet == null) return null;

        Set<IPAddressVO> toIPAddressVOSet = new HashSet<IPAddressVO>();
        for (IPAddress fromIPAddress : fromIPAddressSet)
            toIPAddressVOSet.add(toIPAddressVO(fromIPAddress));
        return toIPAddressVOSet;
    }

    public static IPAddressVO toIPAddressVO(IPAddress fromIPAddress) {
        if (fromIPAddress == null) return null;

        IPAddressVO toIPAddressVO = new IPAddressVO();
        toIPAddressVO(fromIPAddress, toIPAddressVO);
        return toIPAddressVO;
    }

    private static void toIPAddressVO(IPAddress fromIPAddress, IPAddressVO toIPAddressVO) {
        if (fromIPAddress == null) throw new IllegalArgumentException("null fromIPAddress");
        if (toIPAddressVO == null) throw new IllegalArgumentException("null toIPAddressVO");

        toIPAddressVO.setAddress(fromIPAddress.getAddress());
        if (fromIPAddress.isIPv4())
            toIPAddressVO.setType(IPAddressVO.Type.IPv4);
        else
            toIPAddressVO.setType(IPAddressVO.Type.IPv6);
    }

    // ---------------------- Contact convert methods ----------------------
    public static List<ContactVO> toContactVOList(List<Contact> fromContactsList) throws InvalidCountryCodeException, InvalidEmailException {
        if (fromContactsList == null) return null;

        List<ContactVO> toContactVOList = new ArrayList<ContactVO>();
        for (Contact fromContact : fromContactsList)
            toContactVOList.add(toContactVO(fromContact));
        return toContactVOList;
    }

    public static ContactVO toContactVO(Contact fromContact) throws InvalidCountryCodeException, InvalidEmailException {
        if (fromContact == null) return null;

        ContactVO toContactVO = new ContactVO();
        toContactVO(fromContact, toContactVO);
        return toContactVO;
    }

    private static void toContactVO(Contact fromContact, ContactVO toContactVO) throws InvalidCountryCodeException, InvalidEmailException {
        if (fromContact == null) throw new IllegalArgumentException("null fromContact");
        if (toContactVO == null) throw new IllegalArgumentException("null toContactVO");


        toContactVO.setAddress(toAddressVO(fromContact.getAddress()));

        toContactVO.setObjId(fromContact.getObjId());
        toContactVO.setName(fromContact.getName());
        toContactVO.setOrganization(fromContact.getOrganization());
        toContactVO.setJobTitle(fromContact.getJobTitle());
        toContactVO.setAddress(toAddressVO(fromContact.getAddress()));
        toContactVO.setFaxNumber(fromContact.getFaxNumber());
        toContactVO.setAltFaxNumber(fromContact.getAltFaxNumber());
        toContactVO.setPhoneNumber(fromContact.getPhoneNumber());
        toContactVO.setAltPhoneNumber(fromContact.getAltPhoneNumber());
        toContactVO.setEmail(fromContact.getEmail());
        toContactVO.setPrivateEmail(fromContact.getPrivateEmail());
        toContactVO.setRole(fromContact.isRole());

        if (fromContact.getTrackData() != null) {
            toContactVO.setCreated(fromContact.getCreated());
            toContactVO.setCreatedBy(fromContact.getCreatedBy());
            toContactVO.setModified(fromContact.getModified());
            toContactVO.setModifiedBy(fromContact.getModifiedBy());
        }
    }

// ---------------------- Address convert methods ----------------------

    public static List<AddressVO> toAddressVOList(List<Address> fromAddressList) throws InvalidCountryCodeException {
        if (fromAddressList == null) return null;

        List<AddressVO> toAddressListV0 = new ArrayList<AddressVO>();
        for (Address fromAddress : fromAddressList)
            toAddressListV0.add(toAddressVO(fromAddress));
        return toAddressListV0;
    }

    public static AddressVO toAddressVO(Address fromAddress) throws InvalidCountryCodeException {
        if (fromAddress == null) return null;

        AddressVO addressVO = new AddressVO();
        toAddressVO(fromAddress, addressVO);
        return addressVO;
    }

    private static void toAddressVO(Address fromAddress, AddressVO toAddressVO) throws InvalidCountryCodeException {
        if (fromAddress == null) throw new IllegalArgumentException("null fromAddress");
        if (toAddressVO == null) throw new IllegalArgumentException("null toAddressVO");

        toAddressVO.setTextAddress(fromAddress.getTextAddress());
        toAddressVO.setCountryCode(fromAddress.getCountryCode());
    }

    private static List<String> toStringList(List<String> src) {
        List<String> dest = new ArrayList<String>();
        for(String srcStr : src)
            dest.add(srcStr);
        return dest;
    }
}
