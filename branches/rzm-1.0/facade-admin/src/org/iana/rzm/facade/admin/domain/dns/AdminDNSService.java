package org.iana.rzm.facade.admin.domain.dns;

import org.iana.rzm.common.exceptions.InfrastructureException;

import java.io.IOException;

/**
 * This service provides the DNS-related functionality to the administrators of RZM.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface AdminDNSService {

    public void exportAll() throws IOException, InfrastructureException;
    public String exportZoneFile() throws  InfrastructureException;

    public String getExportFileName();

}
