package org.iana.rzm.facade.system.converter;

import org.iana.rzm.domain.*;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.system.*;
import org.iana.rzm.user.Role;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

public class ToVOConverter {

    private static Map<Domain.State, DomainVO.State> domainStates = new HashMap<Domain.State, DomainVO.State>();
    private static Map<Domain.Status, DomainVO.Status> domainStatus = new HashMap<Domain.Status, IDomainVO.Status>();

    static {
        domainStates.put(Domain.State.NO_ACTIVITY, DomainVO.State.NO_ACTIVITY);
        domainStates.put(Domain.State.OPERATIONS_PENDING, DomainVO.State.OPERATIONS_PENDING);
        domainStates.put(Domain.State.THIRD_PARTY_PENDING, DomainVO.State.THIRD_PARTY_PENDING);

        domainStatus.put(Domain.Status.ACTIVE, DomainVO.Status.ACTIVE);
        domainStatus.put(Domain.Status.CLOSED, DomainVO.Status.CLOSED);
        domainStatus.put(Domain.Status.NEW, DomainVO.Status.NEW);
    }

// ---------------------- Domain convert methods ----------------------
    public static DomainVO toDomainVO(Domain fromDomain) throws InvalidNameException {
        if (fromDomain == null) return null;

        DomainVO toDomainVO = new DomainVO();
        toDomainVO(fromDomain, toDomainVO);
        return toDomainVO;
    }

    private static void toDomainVO(Domain fromDomain, DomainVO toDomainVO) throws InvalidNameException {
        if (fromDomain == null) throw new IllegalArgumentException("null fromDomain");
        if (toDomainVO == null) throw new IllegalArgumentException("null toDomainVO");


        toSimpleDomainVO(fromDomain, toDomainVO);
        toDomainVO.setSupportingOrg(toContactVO(fromDomain.getSupportingOrg()));

        toDomainVO.setAdminContacts(toContactVOList(fromDomain.getAdminContacts()));

        toDomainVO.setTechContacts(toContactVOList(fromDomain.getTechContacts()));

        toDomainVO.setNameServers(toHostVOList(fromDomain.getNameServers()));

        toDomainVO.setRegistryUrl(fromDomain.getRegistryUrl());

        if (fromDomain.getWhoisServer() != null) toDomainVO.setWhoisServer(new Name(fromDomain.getWhoisServer()));
        
        toDomainVO.setBreakpoints(toBreakpointVOSet(fromDomain.getBreakpoints()));

        toDomainVO.setSpecialInstructions(fromDomain.getSpecialInstructions());

        toDomainVO.setStatus(toStatusVO(fromDomain.getStatus()));
        toDomainVO.setState(toStateVO(fromDomain.getState()));
    }

    public static SimpleDomainVO toSimpleDomainVO(Domain fromDomain) {
        if (fromDomain == null) return null;

        SimpleDomainVO simpleDomainVO = new SimpleDomainVO();
        toSimpleDomainVO(fromDomain, simpleDomainVO);
        return simpleDomainVO;
    }
    
    private static void toSimpleDomainVO(Domain fromDomain, SimpleDomainVO toSimpleDomainVO ) {
        if (fromDomain == null) throw new IllegalArgumentException("null fromDomain");
        if (toSimpleDomainVO == null) throw new IllegalArgumentException("null toSimpleDomainVO");

        toSimpleDomainVO.setObjId (fromDomain.getObjId());
        toSimpleDomainVO.setName  (fromDomain.getName());

        if (fromDomain.getTrackData() != null) {
            toSimpleDomainVO.setCreated    (fromDomain.getCreated());
            toSimpleDomainVO.setCreatedBy  (fromDomain.getCreatedBy());
            toSimpleDomainVO.setModified   (fromDomain.getModified());
            toSimpleDomainVO.setModifiedBy (fromDomain.getModifiedBy());
        }
    }

// ---------------------- State convert methods ----------------------

    public static IDomainVO.State toStateVO (Domain.State fromState) {
        if (fromState == null) return null;

        IDomainVO.State stateVO = domainStates.get(fromState);
        CheckTool.checkNull(stateVO, "Unknown domain state " + fromState);
        return stateVO;
    }

// ---------------------- Status convert methods ----------------------

    public static IDomainVO.Status toStatusVO (Domain.Status fromStatus) {
        if (fromStatus == null) return null;

        if (fromStatus == Domain.Status.ACTIVE)
            return IDomainVO.Status.ACTIVE;
        else if (fromStatus == Domain.Status.CLOSED)
                return IDomainVO.Status.CLOSED;
            else
                return IDomainVO.Status.NEW;
    }

// ---------------------- Role convert methods ----------------------
    
    public static RoleVO.Type toRoleTypeVO(Role.Type fromRoleType) {
        if (fromRoleType == null) return null;

        if (fromRoleType == Role.Type.AC)
            return SystemRoleVO.SystemType.AC;
        else if (fromRoleType == Role.Type.SO)
                return SystemRoleVO.SystemType.SO;
            else
                return SystemRoleVO.SystemType.TC;
    }

// ---------------------- Breakpoint convert methods ----------------------

    public static Set<IDomainVO.Breakpoint> toBreakpointVOSet(Set<Domain.Breakpoint> fromBreakpointSet) {
        if (fromBreakpointSet == null) return null;

        Set<IDomainVO.Breakpoint> toBreakpointVOSet = new HashSet<IDomainVO.Breakpoint>();
        for(Domain.Breakpoint breakpoint : fromBreakpointSet)
            toBreakpointVOSet.add(toBreakpointVO(breakpoint));
        return toBreakpointVOSet;
    }

    public static IDomainVO.Breakpoint toBreakpointVO (Domain.Breakpoint fromBreakpoint) {
        if (fromBreakpoint == null) return null;

        if(fromBreakpoint == Domain.Breakpoint.AC_CHANGE_EXT_REVIEW)
            return IDomainVO.Breakpoint.AC_CHANGE_EXT_REVIEW;
        else if(fromBreakpoint == Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW)
                return IDomainVO.Breakpoint.ANY_CHANGE_EXT_REVIEW;
        else if(fromBreakpoint == Domain.Breakpoint.NS_CHANGE_EXT_REVIEW)
                return IDomainVO.Breakpoint.NS_CHANGE_EXT_REVIEW;
        else if(fromBreakpoint == Domain.Breakpoint.SO_CHANGE_EXT_REVIEW)
                return IDomainVO.Breakpoint.SO_CHANGE_EXT_REVIEW;
        else
            return IDomainVO.Breakpoint.TC_CHANGE_EXT_REVIEW;
    }

// ---------------------- Host convert methods ----------------------

    public static List<HostVO> toHostVOList(List<Host> fromHostsList) {
        if (fromHostsList == null) return null;

        List<HostVO> toHostVOList = new ArrayList<HostVO>();
        for(Host fromHost : fromHostsList)
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

        toHostVO.setObjId     (fromHost.getObjId());
        toHostVO.setAddresses (toIPAddressVOSet(fromHost.getAddresses()));
        toHostVO.setName      (fromHost.getName());
        toHostVO.setShared    (fromHost.isShared());

        if (fromHost.getTrackData() != null) {
            toHostVO.setCreated     (fromHost.getCreated());
            toHostVO.setCreatedBy   (fromHost.getCreatedBy());
            toHostVO.setModified    (fromHost.getModified());
            toHostVO.setModifiedBy  (fromHost.getModifiedBy());
        }
    }

// ---------------------- IPAddress convert methods ----------------------

    public static Set<IPAddressVO> toIPAddressVOSet(Set<IPAddress> fromIPAddressSet) {
        if (fromIPAddressSet == null) return null;

        Set<IPAddressVO>toIPAddressVOSet = new HashSet<IPAddressVO>();
        for(Iterator i = fromIPAddressSet.iterator(); i.hasNext();) {
            IPAddress fromIPAddress = (IPAddress) i.next();
            toIPAddressVOSet.add(toIPAddressVO(fromIPAddress));
        }
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
    public static List<ContactVO> toContactVOList(List<Contact> fromContactsList) {
        if (fromContactsList == null) return null;

        List<ContactVO> toContactVOList = new ArrayList<ContactVO>();
        for (Contact fromContact : fromContactsList)
            toContactVOList.add(toContactVO(fromContact));
        return toContactVOList;
    }

    public static ContactVO toContactVO(Contact fromContact) {
        if(fromContact == null) return null;

        ContactVO toContactVO = new ContactVO();
        toContactVO(fromContact, toContactVO);
        return toContactVO;
    }

    private static void toContactVO(Contact fromContact, ContactVO toContactVO) {
        if (fromContact == null) throw new IllegalArgumentException("null fromContact");
        if (toContactVO == null) throw new IllegalArgumentException("null toContactVO");


        toContactVO.setAddresses(toAddressVOList(fromContact.getAddresses()));

        toContactVO.setObjId        (fromContact.getObjId());
        toContactVO.setEmails       (fromContact.getEmails());
        toContactVO.setFaxNumbers   (fromContact.getFaxNumbers());
        toContactVO.setName         (fromContact.getName());
        toContactVO.setPhoneNumbers (fromContact.getPhoneNumbers());
        toContactVO.setRole         (fromContact.isRole());

        if (fromContact.getTrackData() != null) {
            toContactVO.setCreated    (fromContact.getCreated());
            toContactVO.setCreatedBy  (fromContact.getCreatedBy());
            toContactVO.setModified   (fromContact.getModified());
            toContactVO.setModifiedBy (fromContact.getModifiedBy());
        }
    }

// ---------------------- Address convert methods ----------------------

    public static List<AddressVO> toAddressVOList(List<Address> fromAddressList) {
        if (fromAddressList == null) return null;

        List<AddressVO> toAddressListV0 = new ArrayList<AddressVO>();
        for (Address fromAddress : fromAddressList)
            toAddressListV0.add(toAddressVO(fromAddress));
        return toAddressListV0;
    }

    public static AddressVO toAddressVO(Address fromAddress) {
        if (fromAddress == null) return null;

        AddressVO addressVO = new AddressVO();
        toAddressVO(fromAddress, addressVO);
        return addressVO;
    }

    private static void toAddressVO(Address fromAddress, AddressVO toAddressVO) {
        if (fromAddress == null) throw new IllegalArgumentException("null fromAddress");
        if (toAddressVO == null) throw new IllegalArgumentException("null toAddressVO");

        toAddressVO.setCity        (fromAddress.getCity());
        toAddressVO.setCountryCode (fromAddress.getCountryCode());
        toAddressVO.setPostalCode  (fromAddress.getPostalCode());
        toAddressVO.setState       (fromAddress.getState());
        toAddressVO.setStreet      (fromAddress.getStreet());
    }
}
