package org.iana.rzm.facade.system;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.exceptions.InvalidNameException;

/**
 * @author Piotr Tkaczyk
 */

public class ToVOConverter {
    public static void convertToDomainVO(Domain fromDomain, DomainVO toDomainVO) {
        try {
            convertToSimpleDomainVO(fromDomain, toDomainVO);
            toDomainVO.setWhoisServer(new Name(fromDomain.getWhoisServer()));
        } catch (InvalidNameException e) {
            //todo exception unreachable in conversion
        }
    }

    public static void convertToSimpleDomainVO(Domain fromDomain, SimpleDomainVO toSimpleDomainVO ) {
//        toSimpleDomainVO.setCreated(fromDomain.getCreated());
//        toSimpleDomainVO.setCreatedBy(fromDomain.getCreatedBy());
//        toSimpleDomainVO.setModified(fromDomain.getModified());
//        toSimpleDomainVO.setModifiedBy(fromDomain.getModifiedBy());
        toSimpleDomainVO.setName(fromDomain.getName());
        toSimpleDomainVO.setObjId(fromDomain.getObjId());
    }
}
