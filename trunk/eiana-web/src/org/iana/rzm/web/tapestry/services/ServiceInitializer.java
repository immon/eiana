package org.iana.rzm.web.tapestry.services;

import org.iana.rzm.facade.common.RZMStatefulService;

public interface ServiceInitializer {

    public Object getStateObject(String name);

    public <T extends RZMStatefulService> T getBean(String name);
    public <T> T getBean(String name, Class<T> type);


}
