/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.consultationworkflow.api.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaService;
import org.openmrs.module.consultationworkflow.api.WorkflowService;
import org.openmrs.module.consultationworkflow.api.dao.BaseDao;
import org.openmrs.module.consultationworkflow.api.dao.WorkflowDataDao;
import org.openmrs.module.consultationworkflow.api.dao.search.WorkflowDataSearchCriteria;
import org.openmrs.module.consultationworkflow.model.WorkflowConfig;
import org.openmrs.module.consultationworkflow.model.WorkflowData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class WorkflowServiceImpl extends BaseOpenmrsService implements WorkflowService {
	
	private BaseDao<WorkflowConfig> workflowConfigDao;
	
	private WorkflowDataDao workflowDataDao;
	
	private EligibilityCriteriaService eligibilityCriteriaService;
	
	@Override
	public List<WorkflowConfig> getWorkflows() {
		return workflowConfigDao.findAll();
	}
	
	@Override
    public WorkflowConfig saveWorkflow(WorkflowConfig workflow) {
        // Handle associations for all new criteria.
        workflow.getCriteria().stream()
                .filter(c -> c.getId() == null)
                .forEach(c -> {
                    c.setCreator(Context.getAuthenticatedUser());
                    c.setDateCreated(new Date());
                    c.setWorkflowConfig(workflow);
                });
        return workflowConfigDao.createOrUpdate(workflow);
    }
	
	@Override
	public WorkflowConfig getWorkflowByUuid(String uuid) throws APIException {
		return workflowConfigDao.get(uuid).get();
	}
	
	@Override
    public WorkflowData saveWorkflowData(WorkflowData workflowData) throws APIException {
        workflowData.getSteps().stream()
                .filter(s -> s.getId() == null)
                .forEach(s -> {
                    s.setCreator(Context.getAuthenticatedUser());
                    s.setDateCreated(new Date());
                    s.setWorkflowData(workflowData);
                });
        return workflowDataDao.createOrUpdate(workflowData);
    }
	
	@Override
	public WorkflowData getWorkflowDataByUuid(String uuid) throws APIException {
		return workflowDataDao.get(uuid).get();
	}
	
	@Override
	public List<WorkflowData> getWorkflowDataByPatient(Patient patient) throws APIException {
		WorkflowDataSearchCriteria criteria = new WorkflowDataSearchCriteria();
		criteria.setPatient(patient);
		return workflowDataDao.getWorkflowDataByCriteria(criteria);
	}
	
	@Override
    public List<WorkflowConfig> getPatientEligibleWorkflows(String patientUuid) throws APIException {

        List<WorkflowConfig> allWorkflows = getWorkflows();
        if (allWorkflows.isEmpty()) {
            return allWorkflows;
        }

        return allWorkflows.stream()
                .filter(WorkflowConfig::getPublished)
                .filter(workflow -> {
                    Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
                    if (patient == null) {
                        throw new APIException("Patient not found with UUID: " + patientUuid);
                    }
                    return eligibilityCriteriaService
                            .isPatientEligible(patient, new ArrayList<>(workflow.getCriteria()));
                })
                .collect(Collectors.toList());
    }
}
