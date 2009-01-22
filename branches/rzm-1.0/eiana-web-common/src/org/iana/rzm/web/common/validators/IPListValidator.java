package org.iana.rzm.web.common.validators;

import org.iana.dns.validator.*;
import org.iana.rzm.web.common.model.*;

import java.util.*;

public class IPListValidator {

    private IPAddressValidator validator = IPAddressValidator.getInstance();

    public boolean isValid(List<IPAddressVOWrapper> list) {

        boolean valid = true;
        for (IPAddressVOWrapper ipAddress : list) {
            try {
                if (ipAddress.getType() == IPAddressVOWrapper.Type.IPv4) {
                    validator.validateIPv4(ipAddress.getAddress());
                }else{
                    validator.validateIPv6(ipAddress.getAddress());
                }
            }catch (InvalidIPAddressException e) {
                valid = false;
                break;
            }
        }
        return valid;
    }
}
