package org.openmrs.module.consultationworkflow.api.impl;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluator;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.openmrs.module.consultationworkflow.util.ExpressionEvaluationHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluator for PATIENT_DEMOGRAPHICS type criteria using SpEL
 */
@Component
@AllArgsConstructor
public class PatientDemographicsEvaluator implements EligibilityCriteriaEvaluator {
	
	private ExpressionEvaluationHelper expressionHelper;
	
	@Override
	public boolean evaluate(Patient patient, EligibilityCriteria criteria) {
		if (patient == null || criteria == null || StringUtils.isBlank(criteria.getCondition())) {
			return false;
		}
		
		String condition = criteria.getCondition().trim();
		Map<String, Object> patientValues = extractPatientValues(patient);
		
		// Convert the condition to a valid SpEL expression and evaluate it
		String spelExpression = expressionHelper.convertToSpelExpression(condition);
		return expressionHelper.evaluateBoolean(spelExpression, patientValues);
	}
	
	@Override
	public boolean canHandle(EligibilityCriteria criteria) {
		return criteria != null && EligibilityCriteriaType.PATIENT_DEMOGRAPHICS.equals(criteria.getCriteriaType());
	}
	
	/**
	 * Extracts the relevant patient values needed for evaluation
	 * 
	 * @param patient the patient
	 * @return a map of property names to values
	 */
	private Map<String, Object> extractPatientValues(Patient patient) {
        Map<String, Object> values = new HashMap<>();

        // Calculate age
        if (patient.getBirthdate() != null) {
            LocalDate birthDate = patient.getBirthdate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();
            values.put("age", age);
        } else {
            values.put("age", null);
        }

        // Gender
        values.put("gender", patient.getGender());

        // Other demographics can be added here

        return values;
    }
}
