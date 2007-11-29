package org.iana.rzm.mail.processor.simple.processor;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * It provides a template method that first determines whether a given email data can be processed by this processor
 * and then processes it.
 *
 * @author Patrycja Wegrzynowicz
 */
abstract public class AbstractEmailProcessor implements EmailProcessor {

    /**
     * Determines whether a given email data are acceptable by this processor i.e. this processor
     * is able to process it. If this processor is not able to process the email data, IllegalEmailDataException is thrown.
     *
     * @param data the email data to be processed.
     * @throws IllegalMessageDataException thrown when this processor is not able to process the email data.
     */
    abstract protected void _acceptable(MessageData data) throws IllegalMessageDataException;
    
    abstract protected void _process(Message msg) throws EmailProcessException;

    final public void process(Message msg) throws EmailProcessException {
        CheckTool.checkNull(msg, "message");
        _acceptable(msg.getData());
        _process(msg);
    }

}
