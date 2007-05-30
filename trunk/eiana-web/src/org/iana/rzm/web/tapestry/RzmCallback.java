package org.iana.rzm.web.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.ExternalCallback;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.callback.PageCallback;


public class RzmCallback implements ICallback {

    private ICallback callback;
    private String pageName;
    private Object[] parameters;

    public RzmCallback(String pageName) {
        this(pageName, false, null);
    }

    public RzmCallback(String pageName, boolean external, Object[] parmeters) {
        callback = createCallback(pageName, external, parmeters);
        this.pageName = pageName;
        this.parameters = parmeters;
    }

    public RzmCallback(ICallback callback) {
        this.callback = callback;
    }

    public RzmCallback(String pageName, boolean external, Object[] listenerParameters, String url) {
        this(pageName, external, listenerParameters);
        if(url != null){
            callback =  createRedirectCallback(url);
        }
    }


    public String getPageName() {
        return pageName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void performCallback(IRequestCycle cycle) {
        callback.performCallback(cycle);
    }

    public boolean isExternal() {
        return ExternalCallback.class.isAssignableFrom(callback.getClass());
    }


    private ICallback createCallback(String pageName, boolean external, Object[] parmeters) {
        ICallback callback;
        if (external) {
            callback = new ExternalCallback(pageName, parmeters);
        } else {
            callback = new PageCallback(pageName);
        }

        return callback;
    }

    private ICallback createRedirectCallback(String url){
        return new RedirectCallback(url);
    }

    public boolean isRedirect() {
        return RedirectCallback.class.isAssignableFrom(callback.getClass());
    }
}
