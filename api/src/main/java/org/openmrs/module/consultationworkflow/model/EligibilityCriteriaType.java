package org.openmrs.module.consultationworkflow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EligibilityCriteriaType {
	
	PATIENT_DEMOGRAPHICS("Patient Demographics"), PATIENT_ATTRIBUTES("Patient attributes"), PROGRAM("Program"), PROVIDER_ROLE(
	        "Provider Role"), VISIT_TYPE("Visit Type");
	
	private final String description;
}
