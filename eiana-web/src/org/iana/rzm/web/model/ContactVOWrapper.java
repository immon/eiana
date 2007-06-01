package org.iana.rzm.web.model;

import org.apache.commons.lang.StringUtils;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.system.domain.AddressVO;
import org.iana.rzm.facade.system.domain.ContactVO;
import org.iana.rzm.web.util.DateUtil;
import org.iana.rzm.web.util.ListUtil;

import java.util.*;

public class ContactVOWrapper extends ValueObject {

    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String FAX = "FAX";
    public static final String ROLE = "ROLE";
    public static final String LAST_UPDATED = "LAST_UPDATED";
    public static final String EMAIL = "EMAIL";
    public static final String ALTERNATE_EMAIL = "EMAIL";
    public static final String ADDRESS = "ADDRESS";
    public static final String ORGANISATION = "ORGANIZATION";

    public static final String COUNTRY = "COUNTRY";
    private ContactVO vo;
    private SystemRoleVOWrapper.SystemType type;

    public ContactVOWrapper(ContactVO vo, SystemRoleVOWrapper.SystemType type) {
        this.vo = vo;
        this.type = type;
    }

    public long getId() {
        return vo.getObjId();

    }

    public String getName() {
        return vo.getName();
    }

    public void setName(String name) {
        vo.setName(name);
    }

    public String getOrganization(){
        return vo.getOrganization();
    }

    public void setOrganization(String org){
        CheckTool.checkNull(org, ORGANISATION);
        vo.setOrganization(org);
    }

    public boolean isRole() {
        return vo.isRole();
    }

    public void setRole(boolean role) {
        vo.setRole(role);
    }

    public SystemRoleVOWrapper.SystemType getType() {
        return type;
    }

    public void setPhone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            setPhone(0, phone);
        }
    }

    public String getPhone() {
        return getPhone(0);
    }

    public String getFax() {
        return getFax(0);
    }

    public void setFax(String fax) {
        if (StringUtils.isNotBlank(fax)) {
            setFax(0, fax);
        }
    }

    public String getEmail() {
        return getEmail(0);
    }

    public void setEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            setEmail(0, email);
        }
    }

    public Date getCreated() {
        return vo.getCreated();
    }

    public Date getModified() {
        return vo.getModified();
    }

    public String getLastUpdated() {
        if (getModified() == null) {
            return DateUtil.formatDate(getCreated());
        }

        return DateUtil.formatDate(getModified());
    }

    public String getAddress() {
        return getAddress(0);
    }

    public void setAddress(String address) {
        if (StringUtils.isNotBlank(address)) {
            setAddress(0, address);
        }
    }


    public String getCountry() {
        return getCountry(0);
    }

    public void setCountry(String country) {
        if (StringUtils.isNotBlank(country)) {
            setCountry(0, country);
        }
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(ID, String.valueOf(vo.getObjId()));
        map.put(LAST_UPDATED, getLastUpdated());
        map.put(NAME, vo.getName());
        map.put(PHONE, getPhone());
        map.put(FAX, getFax());
        map.put(ROLE, String.valueOf(isRole()));
        map.put(EMAIL, getEmail());
        map.put(ADDRESS, getAddress());
        map.put(COUNTRY, getCountry());
        map.put(ORGANISATION, getOrganization());
        return map;
    }


    public void save(Map<String, String> attributes) {
        setName(attributes.get(NAME));
        setPhone(attributes.get(PHONE));
        setFax(attributes.get(FAX));
        setEmail(attributes.get(EMAIL));
        setRole(attributes.get(ROLE));
        setAddress(attributes.get(ADDRESS));
        setCountry(attributes.get(COUNTRY));
        setOrganization(attributes.get(ORGANISATION));
    }

    private void setRole(String role) {
        setRole(Boolean.valueOf(role));
    }

    private String getAddress(int index) {
        AddressVO address = getFromList(getAddresses(), index);
        if (address.getTextAddress() == null) {
            return "";
        }

        return address.getTextAddress();
    }

    private void setAddress(int index, String address) {
        AddressVO vo = getFromList(getAddresses(), index);
        vo.setTextAddress(address);
        getAddresses().set(index, vo);
    }

    private String getCountry(int index) {
        AddressVO address = getFromList(getAddresses(), index);
        if (address.getCountryCode() == null) {
            return "";
        }

        return address.getCountryCode();
    }

    private void setCountry(int index, String country) {
        AddressVO vo = getFromList(getAddresses(), index);
        vo.setCountryCode(country);
        getAddresses().set(index, vo);
    }


    private String getPhone(int index) {
        return getFromList(getPhoneNumbers(), index);
    }

    private String getFax(int index) {
        return getFromList(getFaxNumbers(), index);
    }

    private String getEmail(int index) {
        return getFromList(getEmails(), index);
    }

    private void setEmail(int index, String email) {
//        List<String> list = getEmails();
//        list.set(index, email);
        vo.setEmail(email);
    }

    private void setPhone(int index, String phone) {
//        List<String> list = getPhoneNumbers();
//        list.set(index, phone);
        vo.setPhoneNumber(phone);
    }

    private void setFax(int index, String fax) {
//        List<String> list = getFaxNumbers();
//        list.set(index, fax);
        vo.setFaxNumber(fax);
    }

    private List<String> getEmails() {
        List<String> list = new ArrayList<String>();
        if (vo.getEmail() != null) {
            list.add(vo.getEmail());
        }
        return list;
    }


    private List<String> getPhoneNumbers() {
        List<String> list = new ArrayList<String>();
        if (vo.getPhoneNumber() != null) {
            list.add(vo.getPhoneNumber());
        }
        return list;
    }

    private List<String> getFaxNumbers() {
        List<String> list = new ArrayList<String>();
        if (vo.getFaxNumber() != null) {
            list.add(vo.getFaxNumber());
        }
        return list;
    }

    private List<AddressVO> getAddresses() {
        List<AddressVO> list = new ArrayList<AddressVO>();
        if (vo.getAddress() != null) {
            list.add(vo.getAddress());
        }
        return list;
    }

    private String getFromList(List<String> list, int index) {
        return ListUtil.get(list, index, "");
    }

    private AddressVO getFromList(List<AddressVO> list, int index) {
        return ListUtil.get(list, index, new AddressVO());
    }


}
