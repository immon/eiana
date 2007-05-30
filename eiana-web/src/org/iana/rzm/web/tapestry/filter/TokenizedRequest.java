package org.iana.rzm.web.tapestry.filter;

import org.apache.hivemind.util.Defense;
import org.apache.tapestry.web.ServletWebRequest;
import org.apache.tapestry.web.WebSession;
import org.apache.tapestry.web.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TokenizedRequest extends ServletWebRequest {

    public static final String TOKEN_NAME = "postToken";
    public static final String TOKEN_MAP_NAME = "postTokenMap";

    private Map _parameterMap;

    public TokenizedRequest(HttpServletRequest request, HttpServletResponse response, String postToken) {
        super(request, response);

        Map parameterMap = null;
        WebSession session = getSession(true);
        if (session != null)
            parameterMap = (Map) session.getAttribute(TOKEN_MAP_NAME + postToken);
        _parameterMap = parameterMap;
    }

    @SuppressWarnings("unchecked")
    public List getParameterNames() {
        if (_parameterMap == null)
            return null;

        return WebUtils.toSortedList(Collections.enumeration(_parameterMap.keySet()));
    }

    public String[] getParameterValues(String name) {
        if (_parameterMap == null)
            return null;

        Defense.notNull(name, "name");

        Object values = _parameterMap.get(name);
        if (values == null || values instanceof String[])
            return (String[]) values;

        String value = (String) values;

        return new String[]{value};
    }

    public String getParameterValue(String name) {
        if (_parameterMap == null)
            return null;

        Defense.notNull(name, "name");

        Object values = _parameterMap.get(name);
        if (values == null || values instanceof String)
            return (String) values;

        String[] array = (String[]) values;
        return array[0];
    }
}

