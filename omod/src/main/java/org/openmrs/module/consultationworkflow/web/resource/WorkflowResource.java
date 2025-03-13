package org.openmrs.module.consultationworkflow.web.resource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.consultationworkflow.api.WorkflowService;
import org.openmrs.module.consultationworkflow.model.WorkflowConfig;
import org.openmrs.module.consultationworkflow.web.controller.ConsultationWorkflowResourceController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Resource(name = RestConstants.VERSION_1 + ConsultationWorkflowResourceController.CONSULTAION_WORKFLOW_NAMESPACE
        + "/workflow", supportedClass = WorkflowConfig.class, supportedOpenmrsVersions = { "2.6.* - 9.9.*" })
public class WorkflowResource extends DelegatingCrudResource<WorkflowConfig> {
	
	private WorkflowService workflowService;
	
	private DatatypeService datatypeService;
	
	@Override
	public WorkflowConfig save(WorkflowConfig delegate) {
		return getWorkflowService().saveWorkflow(delegate);
	}
	
	@Override
	public Object retrieve(String uuid, RequestContext context) throws ResponseException {
		// Note: representation description in context should always have the property "resourceValueReference"
		// in order for super.retrieve to add this property in the returned object.
		SimpleObject object = (SimpleObject) super.retrieve(uuid, context);
		SimpleObject steps = loadStepsJson(object.get("resourceValueReference"));
		object.put("steps", steps != null ? steps : Collections.emptyList());
		return object;
	}
	
	@Override
	public WorkflowConfig getByUniqueId(String uniqueId) {
		return getWorkflowService().getWorkflowByUuid(uniqueId);
	}
	
	@Override
	public void purge(WorkflowConfig delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Unimplemented method 'purge'");
	}
	
	@Override
	public WorkflowConfig newDelegate() {
		return new WorkflowConfig();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("published");
		description.addProperty("version");
		description.addProperty("resourceValueReference");
		description.addSelfLink();
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation || rep instanceof RefRepresentation) {
			return description;
		} else {
			return null;
		}
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("published");
		description.addProperty("version");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("published");
		description.addProperty("version");
		description.addProperty("resourceValueReference");
		return description;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		List<WorkflowConfig> all = getWorkflowService().getWorkflows();
		return new NeedsPaging<>(all, context);
	}
	
	@Override
	protected void delete(WorkflowConfig delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Unimplemented method 'delete'");
	}
	
	private SimpleObject loadStepsJson(String uuid) {
		ClobDatatypeStorage clob = getDatatypeService().getClobDatatypeStorageByUuid(uuid);
		if (clob == null) {
			return null;
		}
		try {
			return SimpleObject.parseJson(clob.getValue());
		}
		catch (IOException e) {
			throw new APIException("Could not load steps json", e);
		}
	}
	
	private WorkflowService getWorkflowService() {
		if (workflowService == null) {
			workflowService = Context.getService(WorkflowService.class);
		}
		return workflowService;
	}
	
	private DatatypeService getDatatypeService() {
		if (datatypeService == null) {
			datatypeService = Context.getDatatypeService();
		}
		return datatypeService;
	}
}
