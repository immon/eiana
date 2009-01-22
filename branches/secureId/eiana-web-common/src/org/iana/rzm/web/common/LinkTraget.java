package org.iana.rzm.web.common;

import org.apache.tapestry.*;
import org.apache.tapestry.callback.*;

public interface LinkTraget  extends IPage {
    public void setIdentifier(Object id);
    public void setCallback(ICallback callback);
}
