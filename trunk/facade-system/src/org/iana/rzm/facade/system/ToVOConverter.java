package org.iana.rzm.facade.system;

import org.iana.rzm.domain.*;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.facade.common.TrackDataVO;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

public class ToVOConverter {

// ---------------------- Domain convert methods ----------------------
    public static void toDomainVO(Domain fromDomain, DomainVO toDomainVO) throws InvalidNameException {
        toSimpleDomainVO(fromDomain, toDomainVO);
        toDomainVO.setWhoisServer(new Name(fromDomain.getWhoisServer()));
        //todo
    }

    public static void toSimpleDomainVO(Domain fromDomain, SimpleDomainVO toSimpleDomainVO ) {
        toSimpleDomainVO.setName       (fromDomain.getName());
        toSimpleDomainVO.setObjId      (fromDomain.getObjId());

        toSimpleDomainVO.setCreated    (fromDomain.getCreated());
        toSimpleDomainVO.setCreatedBy  (fromDomain.getCreatedBy());
        toSimpleDomainVO.setModified   (fromDomain.getModified());
        toSimpleDomainVO.setModifiedBy (fromDomain.getModifiedBy());
    }

// ---------------------- Host convert methods ----------------------

    public static HostVO toHostVO(Host fromHost) {
        HostVO toHostVO = new HostVO();
        toHostVO(fromHost, toHostVO);
        return toHostVO;
    }

    public static void toHostVO(Host fromHost, HostVO toHostVO) {
        toHostVO.setAddresses(toIPAddressVOSet(fromHost.getAddresses()));
        toHostVO.setName(fromHost.getName());
        toHostVO.setObjId(fromHost.getObjId());
        //todo
        // toHostVO.setShared(); ???

        toHostVO.setCreated(fromHost.getCreated());
        toHostVO.setCreatedBy(fromHost.getCreatedBy());
        toHostVO.setModified(fromHost.getModified());
        toHostVO.setModifiedBy(fromHost.getModifiedBy());
    }

// ---------------------- IPAddress convert methods ----------------------

    public static Set<IPAddressVO> toIPAddressVOSet(Set<IPAddress> fromIPAddressSet) {
        Set<IPAddressVO>toIPAddressVOSet = new HashSet<IPAddressVO>();
        for(Iterator i = fromIPAddressSet.iterator(); i.hasNext();) {
            IPAddress fromIPAddress = (IPAddress) i.next();
            toIPAddressVOSet.add(toIPAddressVO(fromIPAddress));
        }
        return toIPAddressVOSet;
    }

    public static IPAddressVO toIPAddressVO(IPAddress fromIPAddress) {
        IPAddressVO toIPAddressVO = new IPAddressVO();
        toIPAddressVO(fromIPAddress, toIPAddressVO);
        return toIPAddressVO;
    }

    public static void toIPAddressVO(IPAddress fromIPAddress, IPAddressVO toIPAddressVO) {
        toIPAddressVO.setAddress(fromIPAddress.getAddress());
        if (fromIPAddress.isIPv4())
                toIPAddressVO.setType(IPAddressVO.Type.IPv4);
        else
                toIPAddressVO.setType(IPAddressVO.Type.IPv6);
    }

// ---------------------- Contact convert methods ----------------------

    public static void toContactVO(Contact fromContact, ContactVO toContactVO) {
        toContactVO.setAddresses(toAddressVOList(fromContact.getAddresses()));
        toContactVO.setCreated(fromContact.getCreated());
        toContactVO.setCreatedBy(fromContact.getCreatedBy());
        toContactVO.setEmails(fromContact.getEmails());
        toContactVO.setFaxNumbers(fromContact.getFaxNumbers());
        toContactVO.setModified(fromContact.getModified());
        toContactVO.setModifiedBy(fromContact.getModifiedBy());
        toContactVO.setName(fromContact.getName());
        toContactVO.setObjId(fromContact.getObjId());
        toContactVO.setPhoneNumbers(fromContact.getPhoneNumbers());
        // todo
        // toContactVO.setRole(); ??
    }

// ---------------------- TrackData convert methods ----------------------

    /*public static TrackDataVO toTrackData(TrackData fromTrackData) {
        TrackDataVO toTrackDataVO = new TrackDataVO();
        toTrackDataVO(fromTrackData, toTrackDataVO);
        return toTrackDataVO;
    }

    public static void toTrackDataVO(TrackData fromTrackData, TrackDataVO toTrackDataVO) {
        toTrackDataVO.setCreated    (fromTrackData.getCreated());
        toTrackDataVO.setCreatedBy  (fromTrackData.getCreatedBy());
        toTrackDataVO.setModified   (fromTrackData.getModified());
        toTrackDataVO.setModifiedBy (fromTrackData.getModifiedBy());
    }*/

// ---------------------- Address convert methods ----------------------

    public static List<AddressVO> toAddressVOList(List<Address> fromAddressList) {
        List<AddressVO> toAddressListV0 = new ArrayList<AddressVO>();
        for (Address fromAddress : fromAddressList)
            toAddressListV0.add(toAddressVO(fromAddress));
        return toAddressListV0;
    }

    public static AddressVO toAddressVO(Address fromAddress) {
        AddressVO addressVO = new AddressVO();
        toAddressVO(fromAddress, addressVO);
        return addressVO;
    }

    public static void toAddressVO(Address fromAddress, AddressVO toAddressVO) {
        toAddressVO.setCity       (fromAddress.getCity());
        toAddressVO.setCountryCode(fromAddress.getCountryCode());
        toAddressVO.setPostalCode (fromAddress.getPostalCode());
        toAddressVO.setState      (fromAddress.getState());
        toAddressVO.setStreet     (fromAddress.getStreet());
    }
}
