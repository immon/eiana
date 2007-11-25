// Copyright 2006 ICANN. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
//    1. Redistributions of source code must retain the above copyright notice, this list of conditions
//       and the following disclaimer.
//    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
//       and the following disclaimer in the documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY ICANN ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY,
// OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
//  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
//  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those of the authors
// and should not be interpreted as representing official policies,
// either expressed or implied, of ICANN.package org.iana.rzm.web;
package org.iana.rzm.web.pages;

import org.apache.log4j.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.describe.*;
import org.apache.tapestry.pages.Exception;
import org.apache.tapestry.web.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * A custom Exception page that shows a frendly error message in case of an unhanded Exception occur
 */
public abstract class ApplicationException extends Exception {


    @InjectObject("infrastructure:request")
    public abstract WebRequest getRequest();

    public boolean isRzmApplicationException(){
        String exceptionName = getExceptionName();
        return exceptionName != null && exceptionName.contains("RzmApplicationException");
    }

//    @InjectObject("service:rzm.EmailService")
//    public abstract EmailService getEmailService();
//

    public abstract void setError(String error);
    public abstract String getError();

    public abstract void setExceptionName(String name);
    public abstract String getExceptionName();

    public boolean isPageNotFoundException() {
        String exceptionName = getExceptionName();
        return exceptionName != null && exceptionName.contains("PageNotFoundException");
    }

    public boolean isOtherException(){
        return !isPageNotFoundException() && (!isRzmApplicationException());
    }

    @Override
    public void setException(Throwable value) {
        super.setException(value);

        ExceptionReciver receiver = new ExceptionReciver();

        getRequest().describeTo(receiver);

        setExceptionName(value.getClass().getName());

        String message = value.getMessage();

        if (message == null) {
            message = value.getClass().getName();
        }

        String requestDetails = buildRequestDetails(receiver.getServletRequest());

        setError(message);
        if(!isPageNotFoundException()){
            //getEmailService().sendLogMessage(requestDetails, value);
            Logger.getLogger(getClass()).error(requestDetails, value);
        }
    }

    private String buildRequestDetails(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("HttpServletRequest").append("\n");
        builder.append("authType = ").append( request.getAuthType()).append("\n");
        builder.append("characterEncoding = ").append( request.getCharacterEncoding()).append("\n");
        builder.append("contentLength = ").append( request.getContentLength()).append("\n");
        builder.append("contextPath = ").append( request.getContextPath()).append("\n");
        builder.append("contentType = ").append( request.getContentType()).append("\n");
        builder.append("locale = ").append( request.getLocale()).append("\n");
        builder.append("method = ").append( request.getMethod()).append("\n");
        builder.append("pathInfo = ").append( request.getPathInfo()).append("\n");
        builder.append("pathTranslated = ").append( request.getPathTranslated()).append("\n");
        builder.append("protocol = ").append( request.getProtocol()).append("\n");
        builder.append("queryString = ").append( request.getQueryString()).append("\n");
        builder.append("requestURI = ").append( request.getRequestURI()).append("\n");
        builder.append("scheme = ").append( request.getScheme()).append("\n");
        builder.append("secure = ").append( request.isSecure()).append("\n");
        builder.append("serverName = ").append( request.getServerName()).append("\n");
        builder.append("serverPort = ").append( request.getServerPort()).append("\n");
        builder.append("servletPath = ").append( request.getServletPath()).append("\n");
        builder.append("userPrincipal = ").append( request.getUserPrincipal()).append("\n");
        builder.append("remoteAddress = ").append(request.getRemoteAddr()).append("\n");
        builder.append("remoteHost = ").append(request.getRemoteHost()).append("\n");
        builder.append("remotePort = ").append(request.getRemotePort()).append("\n");
        return builder.toString();
    }

    private static class ExceptionReciver implements DescriptionReceiver {

        private HttpServletRequest servletRequest;

        public void describeAlternate(Object alternate) {

            servletRequest = (HttpServletRequest) alternate;
        }

        public void title(String title) {

        }

        public void section(String section) {
        }

        public void property(String key, Object value) {
        }

        public void property(String key, boolean value) {
        }

        public void property(String key, byte value) {
        }

        public void property(String key, short value) {
        }

        public void property(String key, int value) {
        }

        public void property(String key, long value) {
        }

        public void property(String key, float value) {
        }

        public void property(String key, double value) {
        }

        public void property(String key, char value) {
        }

        public void array(String key, Object[] values) {
        }

        public void collection(String key, Collection values) {
        }

        public HttpServletRequest getServletRequest() {
            return servletRequest;
        }
    }

}






