package org.openmrs.module.consultationworkflow.web.resource;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.consultationworkflow.api.WorkflowService;
import org.openmrs.module.consultationworkflow.model.WorkflowData;
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
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Resource(name = RestConstants.VERSION_1 + ConsultationWorkflowResourceController.CONSULTAION_WORKFLOW_NAMESPACE
        + "/workflowdata", supportedClass = WorkflowData.class, supportedOpenmrsVersions = { "2.6.* - 9.9.*" })
public class WorkflowDataResource extends DelegatingCrudResource<WorkflowData> {
	
	private WorkflowService workflowService;
	
	private PatientService patientService;
	
	@Override
	public WorkflowData newDelegate() {
		return new WorkflowData();
	}
	
	@Override
	public WorkflowData save(WorkflowData delegate) {
		return getWorkflowService().saveWorkflowData(delegate);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("workflowConfig");
		description.addProperty("patient");
		description.addProperty("visit");
		description.addProperty("steps");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addSelfLink();
		if (rep instanceof DefaultRepresentation || rep instanceof RefRepresentation) {
			description.addProperty("workflowConfig", Representation.REF);
			description.addProperty("patient", Representation.REF);
			description.addProperty("visit", Representation.REF);
			description.addProperty("steps", Representation.REF);
			return description;
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("workflowConfig", Representation.DEFAULT);
			description.addProperty("patient", Representation.DEFAULT);
			description.addProperty("visit", Representation.DEFAULT);
			description.addProperty("steps", Representation.DEFAULT);
			
			return description;
		} else {
			return null;
		}
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		return getCreatableProperties();
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
		        .property("workflowConfig", new UUIDProperty())
		        .property("patient", new UUIDProperty())
		        .property("visit", new UUIDProperty())
		        .property(
		            "steps",
		            new ArrayProperty(new ObjectProperty(Map.of("stepId", new StringProperty(), "stepName",
		                new StringProperty(), "renderType", new StringProperty(), "dataReference", new StringProperty(),
		                "completed", new BooleanProperty())))).required("workflowConfig").required("patient")
		        .required("visit").required("steps");
	}
	
	@Override
	public WorkflowData getByUniqueId(String uniqueId) {
		return getWorkflowService().getWorkflowDataByUuid(uniqueId);
	}
	
	@Override
	public void purge(WorkflowData delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Unimplemented method 'purge'");
	}
	
	@Override
	protected void delete(WorkflowData delegate, String reason, RequestContext context) throws ResponseException {
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

        List<WorkflowData> workflowDataList = getWorkflowService().getWorkflowDataByPatient(patient);

        return new NeedsPaging<>(workflowDataList, context);
    }
	
	private WorkflowService getWorkflowService() {
		if (workflowService == null) {
			workflowService = Context.getService(WorkflowService.class);
		}
		return workflowService;
	}
	
	private PatientService getPatientService() {
		if (patientService == null) {
			patientService = Context.getPatientService();
		}
		return patientService;
	}
	
}
