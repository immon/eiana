package org.iana.rzm.web.model;

import org.apache.commons.lang.*;

import java.text.*;
import java.util.*;


public class ChangeMessageBuilder {

    private static final String ADD_MESSAGE = "Add  {0} with value {1}";
    private static final String MODIFY_MESSAGE = "{0} {1} from {2} to {3}";
    private static final String DELETE_MESSAGE = "Remove {0} {1}";

    public String message(ChangeVOWrapper change) {
        return getMessageBuilder(change).message(change);
    }

    private MessageBuilder getMessageBuilder(ChangeVOWrapper change) {
        DefaultMessageBuilder builder = new DefaultMessageBuilder();

        if(change.isNameServer()
           && StringUtils.isNotBlank(change.getChangeTitle())
           && (!change.getAction().equals(Change.ChangeType.UPDATE.getDisplayName()))){
            return new UpdateNameServerMessageBuilder(builder);
        }
        return builder;
    }


    private interface MessageBuilder {
        String message(ChangeVOWrapper change);
    }

    private static class DefaultMessageBuilder implements MessageBuilder {

        protected Map<String, String> messageMap = new HashMap<String, String>();

        public DefaultMessageBuilder() {
            messageMap.put(Change.ChangeType.ADDITION.getDisplayName(), ADD_MESSAGE);
            messageMap.put(Change.ChangeType.REMOVAL.getDisplayName(), DELETE_MESSAGE);
            messageMap.put(Change.ChangeType.UPDATE.getDisplayName(), MODIFY_MESSAGE);

        }

        public String message(ChangeVOWrapper change) {
            return buildMessage(change, getParametersForAction(change));
        }

        protected String buildMessage(ChangeVOWrapper change, String[] params) {
            return new MessageFormat(messageMap.get(change.getAction())).format(params);
        }

        protected String[] getParametersForAction(ChangeVOWrapper change) {
            String action = change.getAction();

            if (action.equals(Change.ChangeType.ADDITION.getDisplayName())) {
                return new String[]{change.getFieldName(), change.getValue()};
            } else if (action.equals(Change.ChangeType.UPDATE.getDisplayName())) {
                return new String[]{action, change.getFieldName(), change.getOldValue(), change.getValue()};
            } else if (action.equals(Change.ChangeType.REMOVAL.getDisplayName())) {
                return new String[]{change.getFieldName(), change.getOldValue()};
            }
            return new String[0];
        }
    }

    private static class UpdateNameServerMessageBuilder implements MessageBuilder {
        private DefaultMessageBuilder messageBuilder;

        UpdateNameServerMessageBuilder(DefaultMessageBuilder messageBuilder){
            this.messageBuilder = messageBuilder;
        }

        public String message(ChangeVOWrapper change) {
            return "Update Name Server " + change.getChangeTitle() + " " + messageBuilder.message(change);
        }
    }
}