package org.iana.rzm.web.validator;

import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.dns.validator.IPAddressValidator;
import org.iana.rzm.web.model.IPAddressVOWrapper;

import java.util.List;

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
