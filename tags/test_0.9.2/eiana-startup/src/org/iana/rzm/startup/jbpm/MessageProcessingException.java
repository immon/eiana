package org.iana.rzm.startup.jbpm;

import org.jbpm.msg.Message;

/**
 * @author Patrycja Wegrzynowicz
*/
class MessageProcessingException extends Exception {
  private static final long serialVersionUID = 1L;
  Message message;
  public MessageProcessingException(Message message, Throwable cause) {
    super("message "+message+"' couldn't be processed", cause);
    this.message = message;
  }
}
