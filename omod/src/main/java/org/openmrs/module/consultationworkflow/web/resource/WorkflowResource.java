package org.openmrs.module.consultationworkflow.web.resource;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.consultationworkflow.api.ConsultationWorkflowService;
import org.openmrs.module.consultationworkflow.model.ConsultationWorkflowConfig;
import org.openmrs.module.consultationworkflow.web.controller.ConsultationWorkflowResourceController;
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
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + ConsultationWorkflowResourceController.CONSULTAION_WORKFLOW_NAMESPACE
        + "/workflow", supportedClass = ConsultationWorkflowConfig.class, supportedOpenmrsVersions = { "2.6.* - 9.9.*" })
public class WorkflowResource extends DelegatingCrudResource<ConsultationWorkflowConfig> {
	
	private ConsultationWorkflowService consultationWorkflowService;
	
	public WorkflowResource() {
	}
	
	public WorkflowResource(ConsultationWorkflowService consultationWorkflowService) {
		this.consultationWorkflowService = consultationWorkflowService;
	}
	
	@Override
	public ConsultationWorkflowConfig save(ConsultationWorkflowConfig delegate) {
		throw new UnsupportedOperationException("Unimplemented method 'save'");
	}
	
	@Override
	public ConsultationWorkflowConfig getByUniqueId(String uniqueId) {
		throw new UnsupportedOperationException("Unimplemented method 'getByUniqueId'");
	}
	
	@Override
	public void purge(ConsultationWorkflowConfig delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Unimplemented method 'purge'");
	}
	
	@Override
	public ConsultationWorkflowConfig newDelegate() {
		return new ConsultationWorkflowConfig();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("published");
		description.addProperty("version");
		description.addSelfLink();
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation || rep instanceof RefRepresentation) {
			return description;
		} else {
			return null;
		}
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		List<ConsultationWorkflowConfig> all = getConsultationWorkflowService().getWorkflows();
		return new NeedsPaging<>(all, context);
	}
	
	@Override
	protected void delete(ConsultationWorkflowConfig delegate, String reason, RequestContext context)
	        throws ResponseException {
		throw new UnsupportedOperationException("Unimplemented method 'delete'");
	}
	
	private ConsultationWorkflowService getConsultationWorkflowService() {
		if (consultationWorkflowService == null) {
			consultationWorkflowService = Context.getService(ConsultationWorkflowService.class);
		}
		return consultationWorkflowService;
	}
}
