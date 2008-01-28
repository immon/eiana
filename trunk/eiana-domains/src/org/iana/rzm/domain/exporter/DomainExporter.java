package org.iana.rzm.domain.exporter;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;

import java.io.*;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class DomainExporter {

    private static final String OLD_FILE = ".old";

    private String propertyFileName;
    private String exportFileName;

    public DomainExporter(String propertyFileName, String exportFileName) {
        CheckTool.checkEmpty(propertyFileName, "property file name is empty or null");
        CheckTool.checkEmpty(exportFileName, "export file name is empty or null");
        this.propertyFileName = propertyFileName;
        this.exportFileName = exportFileName;
    }

    public void exportToXML(List<Domain> domains) {
        if (domains != null && !domains.isEmpty())
            try {
                File f = new File(exportFileName);
                if (f.exists())
                    copy(f, new File(exportFileName + OLD_FILE));

                f = new File(exportFileName);
                Writer writer = new FileWriter(f);
                parseToXML(domains, writer);
                writer.close();
            } catch (IOException e) {
                throw new DomainExporterException(e);
            }
    }

    protected void parseToXML(List<Domain> domains, Writer writer) {
        try {
            DomainDecorator domainDecorator = new DomainDecorator(domains);
            Environment env = DPConfig.getEnvironment(propertyFileName);
            DynaXMLParser parser = new DynaXMLParser();
            parser.toXML(domainDecorator, writer, env);
        } catch (DynaXMLException e) {
            throw new DomainExporterException(e);
        }
    }

    void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}