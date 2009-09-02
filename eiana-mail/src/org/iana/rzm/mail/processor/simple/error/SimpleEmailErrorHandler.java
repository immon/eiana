package org.iana.rzm.mail.processor.simple.error;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class SimpleEmailErrorHandler implements EmailErrorHandler {

    private static Logger logger = Logger.getLogger(SimpleEmailErrorHandler.class);

    private Map<String, EmailErrorHandler> errorHandlers;

    public SimpleEmailErrorHandler(Map<String, EmailErrorHandler> errorHandlers) {
        CheckTool.checkNull(errorHandlers, "error handlers");
        this.errorHandlers = errorHandlers;
    }

    public void error(String from, String subject, String content, Exception e) {
        String simpleName = e.getClass().getSimpleName();

        EmailErrorHandler errorHandler = errorHandlers.get(simpleName);

        if (errorHandler == null) {
            logger.error("There is no defined error handler for exception: " + e.getClass().getSimpleName(), e);
        } else {
            errorHandler.error(from, subject, content, e);
        }
    }



   
}
