package org.iana.rzm.web.model;

public class ChangeVOWrapper extends ValueObject {

    private Change change;
    private String title;

    public ChangeVOWrapper(Change change, String title){

        this.change = change;
        this.title = title;
    }

    public String getFieldName() {
        return change.getFiledName();
    }

    public String getOldValue() {
        return change.getOldValue();
    }

    public String getValue() {
        return change.getNewValue();
    }

    public String getAction() {
        return change.getAction();
    }

    public String getChangeTitle(){
        return title;
    }


}
