/**
 * org.iana.rzm.trans.FakeTicketingService
 * (C) Research and Academic Computer Network - NASK
 * lukaszz, 2007-03-13, 11:18:17
 */
package org.iana.rzm.trans;

import org.iana.ticketing.TicketingService;

public class FakeTicketingService implements TicketingService {
    public long generateID() {
        return 0;  
    }
}
