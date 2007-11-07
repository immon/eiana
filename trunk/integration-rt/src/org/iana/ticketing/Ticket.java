package org.iana.ticketing;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public interface Ticket {
    public Long getId();

    public String getTld();

    public List<String> getRequestType();

    public String getIanaState();

    public String getComment();
}
