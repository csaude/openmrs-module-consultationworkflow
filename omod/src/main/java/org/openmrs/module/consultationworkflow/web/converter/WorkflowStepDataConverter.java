package org.openmrs.module.consultationworkflow.web.converter;

import org.openmrs.annotation.Handler;
import org.openmrs.module.consultationworkflow.model.WorkflowStepData;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Handler(supports = WorkflowStepData.class)
public class WorkflowStepDataConverter extends BaseDelegatingConverter<WorkflowStepData> {
	
	@Override
	public WorkflowStepData newInstance(String type) {
		return new WorkflowStepData();
	}
	
	@Override
	public WorkflowStepData getByUniqueId(String string) {
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("stepId");
		description.addProperty("stepName");
		description.addProperty("renderType");
		description.addProperty("completed");
		description.addProperty("dataReference");
		return description;
	}
}
