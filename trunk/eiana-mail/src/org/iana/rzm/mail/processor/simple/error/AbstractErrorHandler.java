package org.iana.rzm.mail.processor.simple.error;

import org.apache.log4j.Logger;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;

/**
 * @author Piotr Tkaczyk
 */

abstract class AbstractErrorHandler implements EmailErrorHandler {

    static Logger logger = Logger.getLogger(AbstractErrorHandler.class);

    protected NotificationProducer producer;

    protected NotificationSender sender;

    private TicketingService ticketingService;


    protected AbstractErrorHandler(NotificationProducer producer, NotificationSender sender, TicketingService ticketingService) {
        CheckTool.checkNull(producer, "notification producer");
        CheckTool.checkNull(sender, "notification sender");
        CheckTool.checkNull(ticketingService, "ticketing service");
        this.producer = producer;
        this.sender = sender;
        this.ticketingService = ticketingService;
    }

    protected void logToRT(TransactionVO transaction, String message) {
        if (transaction == null) {
            logger.error("It is impossible to log info abut exception to RT because transaction is null.");
        } else {
            logToRT(transaction.getTicketID(), message);
        }
    }

    protected void logToRT(Long ticektId, String message) {
        try {
            if (ticektId == null) {
                logger.error("It is impossible to log info abut exception to RT because ticketID is null.");
            } else {
                ticketingService.addComment(ticektId, message);
            }
        } catch (TicketingException e) {
            logger.error("Exception in ticketing service", e);
        }
    }

}
