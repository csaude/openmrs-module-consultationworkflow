package org.openmrs.module.consultationworkflow.api;

import org.openmrs.Patient;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;

/**
 * Interface for evaluating eligibility criteria
 */
public interface EligibilityCriteriaEvaluator {
	
	/**
	 * Evaluates if the given criteria condition is met for the patient
	 * 
	 * @param patient the patient to evaluate
	 * @param criteria the eligibility criteria to evaluate
	 * @return true if the criteria is met, false otherwise
	 */
	boolean evaluate(Patient patient, EligibilityCriteria criteria);
	
	/**
	 * Checks if this evaluator can handle the given criteria type
	 * 
	 * @param criteria the eligibility criteria to check
	 * @return true if this evaluator can handle the criteria, false otherwise
	 */
	boolean canHandle(EligibilityCriteria criteria);
}
