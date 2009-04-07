package org.iana.rzm.facade.admin.domain.dns;

import org.iana.dns.DNSZone;
import org.iana.dns.exporter.DNSExporter;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * The default implementation of AdminDNSService interface.
 *
 * @author Patrycja Wegrzynowicz
 */
public class AdminDNSServiceImpl implements AdminDNSService {

    private static String DEFAULT_EXPORT_DIRECTORY = "/";

    private static String DEFAULT_EXPORT_FILENAME = "export";

    private DNSZoneProducer producer;

    private DNSExporter exporter;

    private String exportDirectory = DEFAULT_EXPORT_DIRECTORY;

    private String exportFilenamePrefix = DEFAULT_EXPORT_FILENAME;

    public AdminDNSServiceImpl(DNSZoneProducer producer, DNSExporter exporter) {
        CheckTool.checkNull(producer, "producer");
        CheckTool.checkNull(exporter, "exporter");
        this.producer = producer;
        this.exporter = exporter;
    }

    public void setExportDirectory(String exportDirectory) throws IOException {
        this.exportDirectory = exportDirectory;
        File dir = new File(exportDirectory);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("cannot create the export directory: " + exportDirectory);
            }
        }
    }

    public void setExportFilenamePrefix(String exportFilenamePrefix) {
        this.exportFilenamePrefix = exportFilenamePrefix;
    }

    public String getExportFileName() {
        return exportFilenamePrefix + System.currentTimeMillis();
    }

    public void exportAll() throws IOException, InfrastructureException {
        try {
            DNSZone zone = producer.getDNSZone();
            File output = getExportFile();
            exporter.export(zone, output);
        } catch (RuntimeException e) {
            throw new InfrastructureException("dsn export", e);
        }
    }

    public String exportZoneFile() throws  InfrastructureException {
        DNSZone zone = producer.getDNSZone();
        StringWriter stringWriter = new StringWriter();
        exporter.export(zone, stringWriter);
        return stringWriter.getBuffer().toString();
    }

    private File getExportFile() {
        return new File(exportDirectory, getExportFileName());
    }
}
