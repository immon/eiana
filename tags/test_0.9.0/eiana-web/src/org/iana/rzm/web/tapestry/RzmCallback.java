package org.iana.rzm.web.tapestry;

import org.apache.tapestry.*;
import org.apache.tapestry.callback.*;


public class RzmCallback implements ICallback {

    private ICallback callback;
    private String pageName;
    private Object[] parameters;
    private long callbackId;

    public RzmCallback(String pageName, long userId) {
        this(pageName, false, null, userId);
    }

    public RzmCallback(String pageName, boolean external, Object[] parmeters, long userId) {
        this.callbackId = userId;
        callback = createCallback(pageName, external, parmeters);
        this.pageName = pageName;
        this.parameters = parmeters;
    }

    public RzmCallback(ICallback callback) {
        this.callback = callback;
    }

    public RzmCallback(String pageName, boolean external, Object[] listenerParameters, String url, long userId) {
        this(pageName, external, listenerParameters, userId);
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

    public long getCallbackId() {
        return callbackId;
    }

    public boolean isCallbackForUser(long userId){
        return callbackId == userId;
    }
}
