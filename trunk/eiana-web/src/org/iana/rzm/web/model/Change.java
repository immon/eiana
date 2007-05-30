package org.iana.rzm.web.model;

import org.apache.commons.lang.StringUtils;
import org.iana.rzm.facade.system.trans.ChangeVO;

public class Change {

    private String filedName;
    private String oldValue;
    private String newValue;
    private String action;
    private static final String BLANK = "Blank";

    enum ChangeType{
        ADDITION("Add"), REMOVAL("Remove"), UPDATE("Update");
        private String displayName;

        ChangeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName(){
            return displayName;
        }
    }

    public Change(String filedName, String oldValue, String newValue, ChangeVO.Type action) {
        this.filedName = filedName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.action = ChangeType.values()[action.ordinal()].getDisplayName();
    }

    public String getAction(){
        return action;
    }


    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getOldValue() {
        if(StringUtils.isEmpty(oldValue)){
            oldValue = BLANK;
        }
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        if(StringUtils.isEmpty(newValue)){
            newValue = BLANK;
        }
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
