package org.iana.rzm.trans.epp;

import org.iana.epp.EPPClient;
import org.iana.epp.internal.verisign.VerisignEPPClient;
import org.apache.log4j.Logger;

import java.io.File;

public class EPPClientFactory {

    private String path;

    public EPPClient getInstance(){
        Logger.getLogger(getClass().getName()).info("Initializing Verisign epp client using path " + path);
        return VerisignEPPClient.getEPPClient(path);
    }

    public void setPath(File config){
        this.path = config.getAbsolutePath();
    }
}
