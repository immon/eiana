package org.iana.rzm.web.tapestry.filter;

import org.apache.tapestry.services.WebRequestServicer;
import org.apache.tapestry.services.WebRequestServicerFilter;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 28, 2007
 * Time: 9:41:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class TokenizedRequestFilter implements WebRequestServicerFilter {
   private HttpServletRequest _request;
   private HttpServletResponse _response;

   public void service(WebRequest request, WebResponse response, WebRequestServicer servicer)
      throws IOException {
      // Looking only for a single parameter named postToken.
      // All exceptions are caught so the current request may be
      // attempted as a last resort.  This will most likely fail since
      // the expected parameters will not have been supplied,
      // but our application ExceptionPage should at least
      // get invoked.
      try
      {
         String postToken = request.getParameterValue(TokenizedRequest.TOKEN_NAME);
         if( postToken != null )
         {
            TokenizedRequest newrequest = new TokenizedRequest(_request, _response, postToken);
            servicer.service(newrequest, response);
            return;
         }
      }
      catch ( Exception e )
      {
      }

      servicer.service(request, response);
   }

   public HttpServletRequest getRequest() {
      return _request;
   }

   public void setRequest(HttpServletRequest _request) {
      this._request = _request;
   }

   public HttpServletResponse getResponse() {
      return _response;
   }

   public void setResponse(HttpServletResponse _response) {
      this._response = _response;
   }
}

