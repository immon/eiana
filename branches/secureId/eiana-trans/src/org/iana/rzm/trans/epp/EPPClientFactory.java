package org.iana.rzm.trans.epp;

import org.iana.epp.EPPClient;
import org.iana.epp.internal.verisign.VerisignEPPClient;

import java.io.File;

public class EPPClientFactory {

    private String path;

    public EPPClient getInstance(){
        return VerisignEPPClient.getEPPClient(path);
    }

    public void setPath(File config){
        this.path = config.getAbsolutePath();
    }
}
