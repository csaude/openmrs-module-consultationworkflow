package org.openmrs.module.consultationworkflow.api.dao.search;

import lombok.Data;
import org.openmrs.Patient;

import java.io.Serializable;

@Data
public class WorkflowDataSearchCriteria implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Patient patient;
}
