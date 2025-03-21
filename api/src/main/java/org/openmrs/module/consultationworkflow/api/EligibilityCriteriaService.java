package org.openmrs.module.consultationworkflow.api;

import org.openmrs.Patient;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;

import java.util.List;

/**
 * Service for evaluating patient eligibility criteria
 */
public interface EligibilityCriteriaService {
	
	/**
	 * Determines if a patient meets a single eligibility criteria
	 * 
	 * @param patient the patient to evaluate
	 * @param criteria the eligibility criteria to check
	 * @return true if the patient meets the criteria, false otherwise
	 */
	boolean isPatientEligible(Patient patient, EligibilityCriteria criteria);
	
	/**
	 * Determines if a patient meets all the given eligibility criteria
	 * 
	 * @param patient the patient to evaluate
	 * @param criteriaList list of eligibility criteria to check
	 * @return true if the patient meets all criteria, false otherwise
	 */
	boolean isPatientEligible(Patient patient, List<EligibilityCriteria> criteriaList);
	
	/**
	 * Gets all criteria from the list that the patient matches
	 * 
	 * @param patient the patient to evaluate
	 * @param criteriaList list of eligibility criteria to check
	 * @return list of criteria that the patient matches
	 */
	List<EligibilityCriteria> getMatchingCriteria(Patient patient, List<EligibilityCriteria> criteriaList);
}
