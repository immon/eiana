package org.iana.rzm.web.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.callback.ICallback;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 28, 2007
 * Time: 2:40:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RedirectCallback implements ICallback {

    private String url;


    public RedirectCallback(String url) {
        this.url = url;
    }

    public void performCallback(IRequestCycle cycle) {
        throw new RedirectException(url);
    }
}
