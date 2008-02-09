package org.iana.rzm.mail.processor.simple.processor;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.simple.data.Message;

import java.util.List;

/**
 * Finds the first processor that is able to process an email i.e. it does not throw IllegalEmailDataException.
 *
 * @author Patrycja Wegrzynowicz
 */
public class CompositeEmailProcessor implements EmailProcessor {

    private List<EmailProcessor> processors;

    public CompositeEmailProcessor(List<EmailProcessor> processors) {
        CheckTool.checkCollectionNull(processors, "email processors");
        this.processors = processors;
    }

    public void process(Message msg) throws EmailProcessException {
        CheckTool.checkNull(msg, "email msg");
        for (EmailProcessor processor : processors) {
            try {
                processor.process(msg);
                return;
            } catch (IllegalMessageDataException e) {
                // try next processor
            }
        }
        throw new IllegalMessageDataException(msg.getData());
    }
}
