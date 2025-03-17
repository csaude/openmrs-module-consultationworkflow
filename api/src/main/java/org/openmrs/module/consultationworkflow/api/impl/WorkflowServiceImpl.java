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
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaService;
import org.openmrs.module.consultationworkflow.api.WorkflowService;
import org.openmrs.module.consultationworkflow.api.dao.BaseDao;
import org.openmrs.module.consultationworkflow.model.WorkflowConfig;
import org.openmrs.module.consultationworkflow.model.WorkflowData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class WorkflowServiceImpl extends BaseOpenmrsService implements WorkflowService {

    private BaseDao<WorkflowConfig> workflowConfigDao;

    private BaseDao<WorkflowData> workflowDataDao;

    private UserService userService;

    private EligibilityCriteriaService eligibilityCriteriaService;

    @Override
    public List<WorkflowConfig> getWorkflows() {
        return workflowConfigDao.findAll();
    }

    @Override
    public WorkflowConfig saveWorkflow(WorkflowConfig workflow) {
        return workflowConfigDao.createOrUpdate(workflow);
    }

    @Override
    public WorkflowConfig getWorkflowByUuid(String uuid) throws APIException {
        return workflowConfigDao.get(uuid).orElseThrow(() -> new APIException("Workflow not found with UUID: " + uuid));
    }

    @Override
    public WorkflowData saveWorkflowData(WorkflowData workflow) throws APIException {
        throw new UnsupportedOperationException("Unimplemented method 'saveWorkflowData'");
    }

    @Override
    public WorkflowData getWorkflowDataByUuid(String uuid) throws APIException {
        throw new UnsupportedOperationException("Unimplemented method 'getWorkflowDataByUuid'");
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
                            .isPatientEligible(patient, new ArrayList<>(workflow.getCriteriaSet()));
                })
                .collect(Collectors.toList());
    }
}
