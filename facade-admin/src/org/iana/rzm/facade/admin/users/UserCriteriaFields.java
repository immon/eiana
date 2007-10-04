package org.iana.rzm.facade.admin.users;

import org.iana.rzm.user.AbstractPassword;
import org.iana.rzm.user.Password;
import org.iana.rzm.user.Role;

import javax.persistence.*;
import java.util.List;

/**
 * The names of the fields that can be passed in Criterion structure to "find" methods
 * of AdminUserService to search system users.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface UserCriteriaFields {

    String OBJ_ID = "objId";

    String FIRST_NAME = "firstName";

    String LAST_NAME = "lastName";

    String ORGANIZATION = "organization";

    String LOGIN_NAME = "loginName";

    String EMAIL = "email";

    String SECURID = "securId";

    String ROLE = "role";

    String ROLE_TYPE = "role.type";
}
