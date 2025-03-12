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
import org.openmrs.module.consultationworkflow.Item;
import org.openmrs.module.consultationworkflow.api.ConsultationWorkflowService;
import org.openmrs.module.consultationworkflow.api.dao.ConsultationWorkflowDao;
import org.openmrs.module.consultationworkflow.model.ConsultationWorkflowConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsultationWorkflowServiceImpl extends BaseOpenmrsService implements ConsultationWorkflowService {
	
	ConsultationWorkflowDao dao;
	
	UserService userService;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(ConsultationWorkflowDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
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
	}
	
	@Override
	public Item getItemByUuid(String uuid) throws APIException {
		return dao.getItemByUuid(uuid);
	}
	
	@Override
	public Item saveItem(Item item) throws APIException {
		if (item.getOwner() == null) {
			item.setOwner(userService.getUser(1));
		}
		
		return dao.saveItem(item);
	}
}
