package org.iana.rzm.web.tapestry.services;

import org.apache.tapestry.*;
import org.apache.tapestry.engine.*;
import org.apache.tapestry.services.*;
import org.apache.tapestry.util.*;
import org.apache.tapestry.web.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.util.*;

import java.io.*;
import java.util.*;

public class WhoisService implements IEngineService {

    private LinkFactory linkFactory;
    private WebResponse response;
    private AdminServices adminServices;

    public void setLinkFactory(LinkFactory linkFactory) {
		this.linkFactory = linkFactory;
	}

    public void setResponse(WebResponse response) {
		this.response = response;
	}

    public ILink getLink(boolean post, java.lang.Object parameter) {
		HashMap<String,Object> parameters = new HashMap<String, Object>();
		parameters.put("parameter", parameter);

		return linkFactory.constructLink(this, post, parameters, true);
	}

	public String getName() {
		return "whoisService";
	}

	public void service(IRequestCycle cycle) throws IOException {
		OutputStream output = response.getOutputStream(new ContentType("text/xml"));
		response.setHeader("Content-disposition", "attachment; filename=\"Whois-"  + DateUtil.todayDate() + ".xml\"");
        String data = adminServices.getWhoIsData();
        output.write(data.getBytes());
		output.flush();
		output.close();
	}

    public void setAdminServices(AdminServices adminServices) {
        this.adminServices = adminServices;
    }
}
