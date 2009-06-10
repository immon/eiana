package org.iana.rzm.facade.system.domain.types;

import org.iana.codevalues.Value;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DomainTypes {

    Set<Value> getDomainTypes();

    boolean hasDomainType(String value);
}
