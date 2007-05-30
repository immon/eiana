package org.iana.rzm.web.tapestry.services;

import org.apache.hivemind.lib.SpringBeanFactoryHolder;
import org.apache.tapestry.engine.state.ApplicationStateManager;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.web.Visit;

public class ServiceInitializerImpl implements ServiceInitializer {

    private ApplicationStateManager applicationStateManager;

    private SpringBeanFactoryHolder beanFactoryHolder;

    public void setApplicationStateManager(ApplicationStateManager applicationStateManager) {
        this.applicationStateManager = applicationStateManager;
    }

    public void setBeanFactoryHolder(SpringBeanFactoryHolder beanFactoryHolder) {
        this.beanFactoryHolder = beanFactoryHolder;
    }


    public Object getStateObject(String name) {
        return applicationStateManager.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends RZMStatefulService> T getBean(String name) {
        T bean = (T) beanFactoryHolder.getBeanFactory().getBean(name);
        Visit visit = (Visit) applicationStateManager.get("visit");
        bean.setUser(visit.getUser().getUser());
        return bean;
    }

    public <T> T getBean(String name, Class<T> type) {
        Object o = beanFactoryHolder.getBeanFactory().getBean(name);
        return type.cast(o);
    }


}
 
