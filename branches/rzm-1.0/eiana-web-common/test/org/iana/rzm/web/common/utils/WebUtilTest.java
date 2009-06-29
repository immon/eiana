package org.iana.rzm.web.common.utils;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Jun 11, 2009
 * Time: 8:05:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebUtilTest {

    @Test
    public void testStrippricentageFromToken()throws Exception{
        Assert.assertEquals(WebUtil.stripPricentageFromToken("1234"), "1234");
        Assert.assertEquals(WebUtil.stripPricentageFromToken("%1234"), "1234");
        Assert.assertEquals(WebUtil.stripPricentageFromToken("%12%34"), "12%34");
        Assert.assertEquals(WebUtil.stripPricentageFromToken("12%34"), "12%34");
        Assert.assertEquals(WebUtil.stripPricentageFromToken("1234%"), "1234%");
    }
}
