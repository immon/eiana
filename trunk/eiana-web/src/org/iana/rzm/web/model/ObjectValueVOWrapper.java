package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.trans.ChangeVO;
import org.iana.rzm.facade.system.trans.ObjectValueVO;
import org.iana.rzm.facade.system.trans.StringValueVO;
import org.iana.rzm.facade.system.trans.ValueVO;

import java.util.List;

public class ObjectValueVOWrapper extends ValueObject implements VlaueVOWrapper{

    private ObjectValueVO vo;

    public ObjectValueVOWrapper(ObjectValueVO vo) {
        this.vo = vo;
    }

    public String getName() {
        return vo.getName();
    }

    public String getValue() {
        List<ChangeVO> changeVOs = vo.getChanges();
        StringBuilder builder = new StringBuilder();

        for (ChangeVO changeVO : changeVOs) {
            ValueVO valueVO = changeVO.getValue();
            if (StringValueVO.class.isAssignableFrom(valueVO.getClass())) {
                StringValueVO svo = (StringValueVO) valueVO;
                builder.append(svo.getNewValue()).append(" ");
            } else {
                builder.append(new ObjectValueVOWrapper((ObjectValueVO) valueVO).getValue());
            }
        }
        return builder.toString();
    }

    public String getOldValue() {
        List<ChangeVO> changeVOs = vo.getChanges();
        StringBuilder builder = new StringBuilder();

        for (ChangeVO changeVO : changeVOs) {
            ValueVO valueVO = changeVO.getValue();
            if (StringValueVO.class.isAssignableFrom(valueVO.getClass())) {
                StringValueVO svo = (StringValueVO) valueVO;
                builder.append(svo.getOldValue()).append(" ");
            } else {
                builder.append(new ObjectValueVOWrapper((ObjectValueVO) valueVO).getOldValue());
            }
        }
        return builder.toString();
    }


}
