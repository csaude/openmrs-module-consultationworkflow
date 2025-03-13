package org.openmrs.module.consultationworkflow.api.dao.impl;

import org.hibernate.SessionFactory;
import org.openmrs.module.consultationworkflow.model.WorkflowConfig;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowConfigDaoImpl extends BaseDaoImpl<WorkflowConfig> {
	
	public WorkflowConfigDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
}
