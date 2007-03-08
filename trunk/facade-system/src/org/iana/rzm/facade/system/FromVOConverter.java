package org.iana.rzm.facade.system;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.exceptions.InvalidNameException;

/**
 * @author Piotr Tkaczyk
 */

public class FromVOConverter {
    public static void convertToSimpleDomain(SimpleDomainVO fromSimpleDomainVO, Domain toDomain) {
        try {
            toDomain.setObjId(fromSimpleDomainVO.getObjId());
            toDomain.setName(fromSimpleDomainVO.getName());
        } catch (InvalidNameException e) {
            //todo exception unreachable in conversion
        }
    }
}
