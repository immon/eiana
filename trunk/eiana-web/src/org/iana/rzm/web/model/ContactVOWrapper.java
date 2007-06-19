package org.iana.rzm.web.model;

import org.apache.commons.lang.StringUtils;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.system.domain.ContactVO;
import org.iana.rzm.web.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContactVOWrapper extends ValueObject {

    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String ALT_PHONE = "ALT_PHONE";
    public static final String FAX = "FAX";
    public static final String ALT_FAX = "ALT_FAX";
    public static final String ROLE = "ROLE";
    public static final String LAST_UPDATED = "LAST_UPDATED";
    public static final String EMAIL = "EMAIL";
    public static final String JOB_TITLE = "JOB_TITLE";
    public static final String PRIVATE_EMAIL = "PRIVATE_EMAIL";
    public static final String ADDRESS = "ADDRESS";
    public static final String ORGANISATION = "ORGANIZATION";
    public static final String COUNTRY = "COUNTRY";

    private ContactVO vo;
    private SystemRoleVOWrapper.SystemType type;
    private static final String DEFAULT_VALUE = "";

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

    private void setName(String name) {
        vo.setName(name);
    }


    public String getJobTitle() {
        return getValue(vo.getJobTitle(), DEFAULT_VALUE);
    }

    private void setJobTitle(String jobTitle) {
        vo.setJobTitle(jobTitle);
    }

    public String getOrganization() {
        return vo.getOrganization();
    }

    private void setOrganization(String org) {
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

    private void setPhone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            vo.setPhoneNumber(phone);
        }
    }

    public String getPhone() {
        return vo.getPhoneNumber();
    }

    public String getAlternatePhone() {
        return getValue(vo.getAltPhoneNumber(), DEFAULT_VALUE);
    }

    private void setAlternatePhone(String phone) {
        if (!(StringUtils.isEmpty(phone) && StringUtils.isEmpty(vo.getAltPhoneNumber()))) {
            vo.setAltPhoneNumber(phone);
        }
    }

    public String getFax() {
        return getValue(vo.getFaxNumber(), DEFAULT_VALUE);
    }

    private void setFax(String fax) {
        vo.setFaxNumber(fax);
    }

    public String getAlternateFax() {
        return getValue(vo.getAltFaxNumber(), DEFAULT_VALUE);
    }

    private void setAlternateFax(String fax) {
        if (!(StringUtils.isEmpty(fax) && StringUtils.isEmpty(vo.getAltFaxNumber()))) {
            vo.setAltFaxNumber(fax);
        }
    }

    public String getEmail() {
        return vo.getEmail();
    }

    private String getValue(String value, String defaultValue) {
        return StringUtils.defaultIfEmpty(value, defaultValue);
    }

    private void setEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            vo.setPublicEmail(email);
        }
    }

    public String getPrivateEmail() {
        return vo.getPrivateEmail();
    }

    private void setPrivateEmail(String email) {
        if (!(StringUtils.isEmpty(email) && StringUtils.isEmpty(vo.getPrivateEmail()))) {
            vo.setPrivateEmail(email);
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
        return vo.getAddress().getTextAddress();
    }

    public void setAddress(String address) {
        if (StringUtils.isNotBlank(address)) {
            vo.getAddress().setTextAddress(address);
        }
    }


    public String getCountry() {
        return vo.getAddress().getCountryCode();
    }

    public void setCountry(String country) {
        if (StringUtils.isNotBlank(country)) {
            vo.getAddress().setCountryCode(country);
        }
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(ID, String.valueOf(vo.getObjId()));
        map.put(LAST_UPDATED, getLastUpdated());
        map.put(NAME, vo.getName());
        map.put(JOB_TITLE, vo.getJobTitle());
        map.put(PHONE, getPhone());
        map.put(ALT_PHONE, getAlternatePhone());
        map.put(FAX, getFax());
        map.put(ALT_FAX, getAlternateFax());
        map.put(ROLE, String.valueOf(isRole()));
        map.put(EMAIL, getEmail());
        map.put(PRIVATE_EMAIL, getPrivateEmail());
        map.put(ADDRESS, getAddress());
        map.put(COUNTRY, getCountry());
        map.put(ORGANISATION, getOrganization());
        return map;
    }


    public void save(Map<String, String> attributes) {
        setName(attributes.get(NAME));
        setJobTitle(attributes.get(JOB_TITLE));
        setPhone(attributes.get(PHONE));
        setAlternatePhone(attributes.get(ALT_PHONE));
        setFax(attributes.get(FAX));
        setAlternateFax(attributes.get(ALT_FAX));
        setEmail(attributes.get(EMAIL));
        setPrivateEmail(attributes.get(PRIVATE_EMAIL));
        setRole(attributes.get(ROLE));
        setAddress(attributes.get(ADDRESS));
        setCountry(attributes.get(COUNTRY));
        setOrganization(attributes.get(ORGANISATION));
    }

    private void setRole(String role) {
        setRole(Boolean.valueOf(role));
    }
}
