/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.consultationworkflow.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
<<<<<<< Updated upstream:api/src/main/java/org/openmrs/module/consultationworkflow/api/impl/ConsultationWorkflowServiceImpl.java
import org.openmrs.module.consultationworkflow.Item;
import org.openmrs.module.consultationworkflow.api.ConsultationWorkflowService;
import org.openmrs.module.consultationworkflow.api.dao.ConsultationWorkflowDao;
import org.openmrs.module.consultationworkflow.model.ConsultationWorkflowConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsultationWorkflowServiceImpl extends BaseOpenmrsService implements ConsultationWorkflowService {
=======
import org.openmrs.module.consultationworkflow.api.WorkflowDataService;
import org.openmrs.module.consultationworkflow.api.dao.impl.WorkflowDataDaoImpl;
import org.openmrs.module.consultationworkflow.model.WorkflowData;

public class WorkflowDataServiceImpl extends BaseOpenmrsService implements WorkflowDataService {
>>>>>>> Stashed changes:api/src/main/java/org/openmrs/module/consultationworkflow/api/impl/WorkflowDataServiceImpl.java
	
	WorkflowDataDaoImpl dao;
	
	UserService userService;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(WorkflowDataDaoImpl dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
<<<<<<< Updated upstream:api/src/main/java/org/openmrs/module/consultationworkflow/api/impl/ConsultationWorkflowServiceImpl.java
	public List<ConsultationWorkflowConfig> getWorkflows() {
		ConsultationWorkflowConfig workflow = new ConsultationWorkflowConfig();
		workflow.setUuid("3d121605-3f5b-49b9-9053-d06d89e92bdc");
		workflow.setName("Dummy workflow");
		workflow.setVersion("1.0");
		workflow.setVoided(false);
		workflow.setPublished(true);
		return List.of(workflow);
	}
	
	@Override
	public ConsultationWorkflowConfig saveWorkflow(ConsultationWorkflowConfig workflow) {
		log.info("Saving workflow: " + workflow);
		return workflow;
=======
	public List<WorkflowData> getWorkflows() {
		return new ArrayList<>();
>>>>>>> Stashed changes:api/src/main/java/org/openmrs/module/consultationworkflow/api/impl/WorkflowDataServiceImpl.java
	}

	@Override
	public WorkflowData getWorkflowByUuid(String uuid) throws APIException {
		return null;
	}

	@Override
	public WorkflowData saveWorkflow(WorkflowData workflow) throws APIException {
		return null;
	}

}
