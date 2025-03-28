package org.openmrs.module.consultationworkflow.api.dao;

import org.openmrs.api.APIException;
import org.openmrs.module.consultationworkflow.api.dao.search.WorkflowDataSearchCriteria;
import org.openmrs.module.consultationworkflow.model.WorkflowData;

import java.util.List;

public interface WorkflowDataDao extends BaseDao<WorkflowData> {
	
	List<WorkflowData> getWorkflowDataByCriteria(WorkflowDataSearchCriteria searchCriteria) throws APIException;
}
