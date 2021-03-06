package org.iana.rzm.facade.admin.users;

/**
 * The names of the fields that can be passed in Criterion structure to "find" methods
 * of AdminUserService to search system users.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface UserCriteria {

    // fields

    String OBJ_ID = "objId";

    String FIRST_NAME = "firstName";

    String LAST_NAME = "lastName";

    String ORGANIZATION = "organization";

    String LOGIN_NAME = "loginName";

    String EMAIL = "email";

    String SECURID = "securId";

    String ROLE = "role";

    String ROLE_TYPE = "role.type";

    String ROLE_DOMAIN = "role.name.name";

    // values

    String SYSTEM_ROLE = "SystemRole";

    String ADMIN_ROLE = "AdminRole";

}
