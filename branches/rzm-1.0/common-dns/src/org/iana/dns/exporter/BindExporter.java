package org.iana.dns.exporter;

import org.iana.dns.DNSZone;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class BindExporter implements DNSExporter {

    public void export(DNSZone zone, File output) throws IOException {
        FileWriter writer = new FileWriter(output);
        try {
            export(zone, writer);
        } finally {
            writer.close();
        }
    }

    public void export(DNSZone zone, Writer writer) {
        BindExport exporter = new BindExport(writer);
        exporter.exportZone(zone);
    }
}
