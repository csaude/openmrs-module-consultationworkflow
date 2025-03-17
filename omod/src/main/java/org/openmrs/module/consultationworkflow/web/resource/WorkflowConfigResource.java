package org.openmrs.module.consultationworkflow.web.resource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.consultationworkflow.api.WorkflowService;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.openmrs.module.consultationworkflow.model.WorkflowConfig;
import org.openmrs.module.consultationworkflow.web.controller.ConsultationWorkflowResourceController;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
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
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.properties.UUIDProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Resource(name = RestConstants.VERSION_1 + ConsultationWorkflowResourceController.CONSULTAION_WORKFLOW_NAMESPACE
        + "/workflowconfig", supportedClass = WorkflowConfig.class, supportedOpenmrsVersions = { "2.6.* - 9.9.*" })
public class WorkflowConfigResource extends DelegatingCrudResource<WorkflowConfig> {
	
	private WorkflowService workflowService;
	
	private DatatypeService datatypeService;
	
	private PatientService patientService;
	
	@Override
	public WorkflowConfig save(WorkflowConfig delegate) {
		return getWorkflowService().saveWorkflow(delegate);
	}
	
	@Override
	public Object retrieve(String uuid, RequestContext context) throws ResponseException {
		// Note: representation description in context should always have the property
		// "resourceValueReference"
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
		if (rep instanceof DefaultRepresentation || rep instanceof RefRepresentation) {
			description.addProperty("criteria", Representation.REF);
			return description;
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("criteria", Representation.DEFAULT);
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
		description.addProperty("resourceValueReference");
		description.addProperty("criteria");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		return getCreatableProperties();
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		model.property("uuid", new UUIDProperty())
		        .property("name", new StringProperty())
		        .property("description", new StringProperty())
		        .property("published", new BooleanProperty())
		        .property("version", new StringProperty())
		        .property("resourceValueReference", new UUIDProperty())
		        .property(
		            "criteria",
		            new ArrayProperty(new ObjectProperty(Map.of("criteriaType", new EnumProperty(
		                    EligibilityCriteriaType.class), "condition", new StringProperty().example("age > 13")))));
		return model;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
		        .property("name", new StringProperty())
		        .property("description", new StringProperty())
		        .property("published", new BooleanProperty())
		        .property("version", new StringProperty())
		        .property("resourceValueReference", new StringProperty().example("uuid"))
		        .property(
		            "criteria",
		            new ArrayProperty(new ObjectProperty(Map.of("criteriaType", new EnumProperty(
		                    EligibilityCriteriaType.class), "condition", new StringProperty().example("age > 13")))));
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
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("patient");
		if (patientUuid == null) {
			return super.doSearch(context);
		}

		Patient patient = getPatientService().getPatientByUuid(patientUuid);
		if (patient == null) {
			return new EmptySearchResult();
		}

		List<WorkflowConfig> workflows = getWorkflowService().getPatientEligibleWorkflows(patientUuid);

		return new NeedsPaging<>(workflows, context);
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
	
	private PatientService getPatientService() {
		if (patientService == null) {
			patientService = Context.getPatientService();
		}
		return patientService;
	}
}
