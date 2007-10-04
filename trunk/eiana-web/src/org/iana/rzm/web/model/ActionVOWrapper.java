package org.iana.rzm.web.model;

import org.apache.commons.lang.*;
import org.iana.rzm.facade.system.trans.vo.changes.*;

import java.util.*;

public class ActionVOWrapper extends ValueObject {

    public static final String MODIFY_NAME_SERVERS = "Modify Name Servers";

    private TransactionActionVO vo;

    public ActionVOWrapper(TransactionActionVO vo) {
        this.vo = vo;
    }

    public String getTitle() {
        return getActionTitle(WordUtils.capitalizeFully(vo.getName()));
    }

    public List<ChangeVOWrapper> getChanges() {
        List<ChangeVO> changes = vo.getChange();
        List<ChangeVOWrapper> wrappers = new ArrayList<ChangeVOWrapper>();
        NameServerChangeFilter nameServerChangeFilter = new NameServerChangeFilter();
        NamServerChangeAdditionFilter namServerChangeAdditionFilter = new NamServerChangeAdditionFilter();
        for (ChangeVO change : changes) {
            ChangeVO removeChangesFilter = nameServerChangeFilter.filter(change);
            ChangeVO additionFilter = namServerChangeAdditionFilter.filter(removeChangesFilter);
            ValueVO valueVO = additionFilter.getValue();
            String name = getChangeTitle(valueVO);
            getChanges(valueVO, wrappers, change.getFieldName(), change.getType(), name);
        }
        return wrappers;
    }

    private String getChangeTitle(ValueVO value) {
        if (StringValueVO.class.isAssignableFrom(value.getClass())) {
            return "";
        }

        return ((ObjectValueVO) value).getName();
    }

    private String getActionTitle(String prefix) {
        if (vo.getChange() == null || vo.getChange().size() == 0) {
            return prefix;
        }

        if (prefix.equals(MODIFY_NAME_SERVERS)) {
            return prefix;
        }

        ChangeVO change = vo.getChange().get(0);

        return prefix + " - " + getChangeTitle(change.getValue());
    }

    private void getChanges(ValueVO value, List<ChangeVOWrapper> list, String name, ChangeVO.Type type, String title) {
        ChangeBuilder builder = new ChangeBuilder();
        if (StringValueVO.class.isAssignableFrom(value.getClass())) {
            StringValueVO svo = (StringValueVO) value;
            list.add(new ChangeVOWrapper(builder.buildChange(svo, name, type), title));
        } else {
            ObjectValueVO obj = (ObjectValueVO) value;
            List<ChangeVO> changes = obj.getChanges();
            for (ChangeVO change : changes) {
                ValueVO valueVO = change.getValue();
                getChanges(valueVO, list, name + "." + change.getFieldName(), change.getType(), title);
            }
        }

    }

    private class NamServerChangeAdditionFilter {

        public ChangeVO filter(ChangeVO change) {
            if (ChangeVO.Type.ADDITION.equals(change.getType())) {
                ValueVO valueVO = change.getValue();
                if (NameServerChangeUtil.isStringValue(valueVO)) {
                    return change;
                }

                if (!NameServerChangeUtil.nameServerAddition(valueVO)) {
                    return change;
                }

                return collectChanges((ObjectValueVO) valueVO);
            }
            return change;
        }

        private ChangeVO collectChanges(ObjectValueVO objectValueVO) {
            String nameServer = objectValueVO.getName();
            List<ChangeVO> changeVOs = objectValueVO.getChanges();
            List<String> ips = new ArrayList<String>();

            for (ChangeVO changeVO : changeVOs) {
                if (changeVO.getFieldName().equals("addresses")) {
                    ObjectValueVO objVo = (ObjectValueVO) changeVO.getValue();
                    ips.add(objVo.getName());
                }
            }

            ChangeVO result = new ChangeVO();
            StringValueVO valueVO = new StringValueVO(null, nameServer + " with IP Addresses " + ips.toString());
            result.setValue(valueVO);
            result.setType(ChangeVO.Type.ADDITION);
            return result;
        }

    }

    private class NameServerChangeFilter {

        public ChangeVO filter(ChangeVO change) {
            if (ChangeVO.Type.REMOVAL.equals(change.getType())) {
                ValueVO valueVO = change.getValue();

                if(NameServerChangeUtil.isStringValue(valueVO)){
                    return change;
                }
                
                if (!NameServerChangeUtil.nameServerRemoval(valueVO)) {
                    return change;
                }

                return NameServerChangeUtil.findNameServerChange((ObjectValueVO) valueVO);
            }
            return change;
        }
    }

    private static class NameServerChangeUtil {

        public static ChangeVO findNameServerChange(ObjectValueVO valueVO) {
            List<ChangeVO> changeVOs = valueVO.getChanges();
            for (ChangeVO changeVO : changeVOs) {
                if (changeVO.getFieldName().equals("name")) {
                    return changeVO;
                }
            }
            return null;
        }

        public static boolean nameServerRemoval(ValueVO valueVO) {
            ChangeVO change = findNameServerChange((ObjectValueVO) valueVO);
            return change != null && change.getType().equals(ChangeVO.Type.REMOVAL);
        }

        public static boolean nameServerAddition(ValueVO valueVO) {
            ChangeVO change = findNameServerChange((ObjectValueVO) valueVO);
            return change != null && change.getType().equals(ChangeVO.Type.ADDITION);
        }

        public static boolean isStringValue(ValueVO valueVO) {
            return StringValueVO.class.isAssignableFrom(valueVO.getClass());
        }
    }
}
