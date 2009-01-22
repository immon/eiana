package org.iana.rzm.web.common.model;

import org.iana.rzm.facade.system.trans.vo.changes.*;

public class StringValueVOWrapper extends ValueObject implements VlaueVOWrapper{

    private StringValueVO vo;

    public StringValueVOWrapper(StringValueVO vo) {
        this.vo = vo;
    }

    public String getValue() {
        return vo.getNewValue();
    }

    public String getOldValue() {
        return vo.getOldValue();
    }

    public String getName() {
        return "";
    }

}
