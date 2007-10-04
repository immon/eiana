package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.trans.vo.ConfirmationVO;

public class ConfirmationVOWrapper extends ValueObject {

    private ConfirmationVO vo;


    public ConfirmationVOWrapper(ConfirmationVO vo){
        this.vo = vo;
    }

    public String getName(){
        return vo.getContactName();
    }

    public String getType(){
        return new SystemRoleVOWrapper(vo.getRole()).getTypeAsString();
    }

    public boolean isConfirmed(){
        return vo.isConfirmed();
    }

    public boolean isNewContact(){
        return vo.isNewContact();
    }

    public String getContact(){
        String contact = getType() + " - " + getName();
        return isNewContact() ? "New " + contact : contact ;
    }
}
