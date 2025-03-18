/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.consultationworkflow.api;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.consultationworkflow.model.WorkflowConfig;
import org.openmrs.module.consultationworkflow.model.WorkflowData;
import org.springframework.transaction.annotation.Transactional;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public interface WorkflowService extends OpenmrsService {
	
	/**
	 * Returns a list of workflows. It can be called by any authenticated user.
	 * 
	 * @return a list of WorkflowConfig objects
	 */
	@Authorized
	List<WorkflowConfig> getWorkflows();
	
	/*
	 * Saves a workflow. It can be called by users with this module's privilege.
	 */
	@Authorized
	@Transactional
	WorkflowConfig saveWorkflow(WorkflowConfig workflowConfig);
	
	/**
	 * Returns a workflow by its uuid. It can be called by any authenticated user. It is fetched in
	 * read only transaction.
	 * 
	 * @param uuid the uuid of the workflow
	 * @return a ConsultationWorkflow object
	 * @throws APIException if the workflow is not found or if there is an error in fetching it
	 */
	@Authorized
	@Transactional(readOnly = true)
	WorkflowConfig getWorkflowByUuid(String uuid) throws APIException;
	
	/**
	 * Saves a workflow. Sets the owner to superuser, if it is not set. It can be called by users
	 * with this module's privilege. It is executed in a transaction.
	 * 
	 * @param workflowData the workflowData to save
	 * @return the saved workflowData
	 * @throws APIException if there is an error in saving the workflowData
	 */
	@Transactional
	WorkflowData saveWorkflowData(WorkflowData workflowData) throws APIException;
	
	@Authorized
	@Transactional(readOnly = true)
	WorkflowData getWorkflowDataByUuid(String uuid) throws APIException;
	
	List<WorkflowConfig> getPatientEligibleWorkflows(String patientUuid) throws APIException;
}
