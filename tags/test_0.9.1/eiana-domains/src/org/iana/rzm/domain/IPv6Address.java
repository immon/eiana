package org.iana.rzm.domain;

import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.dns.validator.IPAddressValidator;

import javax.persistence.Entity;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class IPv6Address extends IPAddress {

    protected IPv6Address() {}

    IPv6Address(String address) throws InvalidIPAddressException {
        super(address, Type.IPv6);
        isValidAddress(address);
        super.setAddress(unfoldIPv6Address(address));
    }
    
    protected void isValidAddress(String address) throws InvalidIPAddressException {
        IPAddressValidator.getInstance().validateIPv6(address);
    }

    private String unfoldIPv6Address(String address) {
        int doubleColonPosition = address.indexOf("::");
        if (doubleColonPosition >= 0) {
            List<String> pieces = new ArrayList<String>();
            for (String piece : Arrays.asList(address.split(":")))
                if (!piece.equals("")) pieces.add(piece);

            StringBuffer unfolded = new StringBuffer();
            boolean wasFilled = false;
            
            for (Iterator iterator = pieces.iterator(); iterator.hasNext();) {
                if (unfolded.toString().length() >= doubleColonPosition && !wasFilled) {
                    for (int i=0; i < 8-pieces.size(); i++) {
                        unfolded.append("0");
                        if (iterator.hasNext()) unfolded.append(":");
                    }
                    wasFilled = true;
                }
                unfolded.append(iterator.next());
                if (!iterator.hasNext() && !wasFilled)
                    for (int i=0; i < 8-pieces.size(); i++)
                        unfolded.append(":0");
                if (iterator.hasNext()) unfolded.append(":");
            }

            return unfolded.toString();
        } else
            return address;
    }
}
