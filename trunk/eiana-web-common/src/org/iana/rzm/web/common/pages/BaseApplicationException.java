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
package org.iana.rzm.web.common.pages;

import org.apache.log4j.Logger;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.pages.Exception;
import org.apache.tapestry.util.exception.ExceptionAnalyzer;
import org.apache.tapestry.util.exception.ExceptionDescription;
import org.apache.tapestry.web.WebRequest;

/**
 * A custom Exception page that shows a frendly error message in case of an unhanded Exception occur
 */
public abstract class BaseApplicationException extends Exception {

    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    @InjectObject("infrastructure:request")
    public abstract WebRequest getRequest();

    public abstract void setError(String error);
    public abstract String getError();

    public abstract void setExceptionName(String name);
    public abstract String getExceptionName();

    public boolean isRzmApplicationException() {
        String exceptionName = getExceptionName();
        return exceptionName != null && exceptionName.contains("RzmApplicationException");
    }

    public boolean isPageNotFoundException() {
        String exceptionName = getExceptionName();
        return exceptionName != null && exceptionName.contains("PageNotFoundException");
    }

    public boolean isAccessDeniedException() {
        String exceptionName = getExceptionName();
        return exceptionName != null && exceptionName.contains("AccessDeniedException");
    }


    public boolean isOtherException() {
        return !isPageNotFoundException() && (!isRzmApplicationException() && (!isAccessDeniedException()));
    }

    @Override
    public void setException(Throwable value) {
        super.setException(value);

        setExceptionName(value.getClass().getName());
        String message = value.getMessage();
        if (message == null) {
            ExceptionAnalyzer analyzer = new ExceptionAnalyzer();
            ExceptionDescription[] exceptions = analyzer.analyze(value);
            StringBuilder builder = new StringBuilder();
            for (ExceptionDescription description : exceptions) {
                builder.append(description.getMessage()).append("\n");
            }
            message = builder.toString();
        }
        setError(message);
        LOGGER.error(message, value);
    }

}





