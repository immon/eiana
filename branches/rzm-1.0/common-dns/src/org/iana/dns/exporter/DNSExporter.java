package org.iana.dns.exporter;

import org.iana.dns.DNSZone;

import java.io.Writer;
import java.io.File;
import java.io.IOException;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DNSExporter {

    public void export(DNSZone zone, File output) throws IOException;
    
    public void export(DNSZone zone, Writer writer);

}
