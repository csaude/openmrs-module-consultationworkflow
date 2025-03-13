/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.consultationworkflow.api.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.db.UserDAO;
import org.openmrs.api.impl.UserServiceImpl;
import org.openmrs.module.consultationworkflow.api.dao.impl.WorkflowDataDaoImpl;
import org.openmrs.module.consultationworkflow.model.WorkflowData;

/**
 * It is an integration test (extends BaseModuleContextSensitiveTest), which verifies DAO methods
 * against the in-memory H2 database. The database is initially loaded with data from
 * standardTestDataset.xml in openmrs-api. All test methods are executed in transactions, which are
 * rolled back by the end of each test method.
 */
public class WorkflowDataDaoTest {
	
	// @Mock
	WorkflowDataDaoImpl dao;
	
	// @Mock
	UserServiceImpl userService;
	
	// @Mock
	UserDAO userDAO;
	
	// @BeforeEach
	// public void setupMocks() {
	// 	MockitoAnnotations.openMocks(this);
	// 	userService = new UserServiceImpl();
	// 	userService.setUserDAO(userDAO);
	// }
	
	@Test
	// @Ignore("Unignore if you want to make the Item class persistable, see also
	// Item and liquibase.xml")
	@Disabled
	public void saveItem_shouldSaveAllPropertiesInDb() {
		// Given
		WorkflowData workflow = new WorkflowData();
		workflow.setId(1000);
		workflow.setCreator(userService.getUser(1));
		
		// When
		dao.createOrUpdate(workflow);
		
		// Let's clean up the cache to be sure getItemByUuid fetches from DB and not
		// from cache
		// Context.flushSession();
		// Context.clearSession();
		
		// Then
		Optional<WorkflowData> savedWorkflowOpt = dao.get(workflow.getUuid());
		assertTrue("Workflow should be present in database", savedWorkflowOpt.isPresent());
		
		WorkflowData savedWorkflow = savedWorkflowOpt.get();
		
		assertThat(savedWorkflow, hasProperty("uuid", is(workflow.getUuid())));
		assertThat(savedWorkflow, hasProperty("owner", is(workflow.getCreator())));
		assertThat(savedWorkflow, hasProperty("id", is(workflow.getId())));
	}
}
