/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
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
public interface WorkflowDataService extends OpenmrsService {
	
	/**
	 * Returns a list of workflows. It can be called by any authenticated user.
	 * 
	 * @return
	 */
	@Authorized
	List<WorkflowConfig> getWorkflows();
	
	/*
	 * Saves a workflow. It can be called by users with this module's privilege.
	 */
	@Authorized
	@Transactional
	WorkflowConfig saveWorkflow(WorkflowConfig workflow);
	
	/**
	 * Returns a workflow by its uuid. It can be called by any authenticated user. It is fetched in
	 * read only transaction.
	 * 
	 * @param uuid the uuid of the workflow
	 * @return a ConsultationWorkflow object
	 * @throws APIException
	 */
	@Authorized
	@Transactional(readOnly = true)
	WorkflowConfig getWorkflowByUuid(String uuid) throws APIException;
	
	/**
	 * Saves a workflow. Sets the owner to superuser, if it is not set. It can be called by users
	 * with this module's privilege. It is executed in a transaction.
	 * 
	 * @param workflow
	 * @return
	 * @throws APIException
	 */
	@Transactional
	WorkflowData saveWorkflowData(WorkflowData workflow) throws APIException;
	
	@Authorized
	@Transactional(readOnly = true)
	WorkflowData getWorkflowDataByUuid(String uuid) throws APIException;
}
