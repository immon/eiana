package org.iana.rzm.facade.admin.config;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.services.RZMStatefulService;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminEmailTemplateManager extends RZMStatefulService {

    void create(EmailTemplateVO template) throws InfrastructureException;

    void update(EmailTemplateVO template) throws InfrastructureException;

    void delete(String templateName) throws InfrastructureException;

    List<EmailTemplateVO> getEmailTemplates() throws InfrastructureException;

}
