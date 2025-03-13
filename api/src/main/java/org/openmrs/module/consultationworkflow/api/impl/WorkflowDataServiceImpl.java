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

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.consultationworkflow.api.WorkflowDataService;
import org.openmrs.module.consultationworkflow.api.dao.BaseDao;
import org.openmrs.module.consultationworkflow.model.WorkflowConfig;
import org.openmrs.module.consultationworkflow.model.WorkflowData;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class WorkflowDataServiceImpl extends BaseOpenmrsService implements WorkflowDataService {
	
	private BaseDao<WorkflowConfig> workflowConfigDao;
	
	private BaseDao<WorkflowData> workflowDataDao;
	
	private UserService userService;
	
	@Override
	public List<WorkflowConfig> getWorkflows() {
		return workflowConfigDao.findAll();
	}
	
	@Override
	public WorkflowConfig saveWorkflow(WorkflowConfig workflow) {
		log.info("Saving workflow: " + workflow);
		return workflow;
	}
	
	@Override
	public WorkflowConfig getWorkflowByUuid(String uuid) throws APIException {
		throw new UnsupportedOperationException("Unimplemented method 'getWorkflowByUuid'");
	}
	
	@Override
	public WorkflowData saveWorkflowData(WorkflowData workflow) throws APIException {
		throw new UnsupportedOperationException("Unimplemented method 'saveWorkflowData'");
	}
	
	@Override
	public WorkflowData getWorkflowDataByUuid(String uuid) throws APIException {
		throw new UnsupportedOperationException("Unimplemented method 'getWorkflowDataByUuid'");
	}
}
