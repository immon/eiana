package org.iana.rzm.domain.exporter;

/**
 * @author Piotr Tkaczyk
 */
public class DomainExporterException extends RuntimeException {

    public DomainExporterException() {
    }

    public DomainExporterException(String string) {
        super(string);
    }

    public DomainExporterException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public DomainExporterException(Throwable throwable) {
        super(throwable);
    }
}
