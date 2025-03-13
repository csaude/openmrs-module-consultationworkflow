package org.openmrs.module.consultationworkflow.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluator;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluator for VISIT_TYPE criteria
 */
@Component
public class VisitTypeEvaluator implements EligibilityCriteriaEvaluator {
	
	// Pattern to match conditions like: firstVisit==true
	private static final Pattern FIRST_VISIT_PATTERN = Pattern.compile("firstVisit\\s*==\\s*(true|false)");
	
	private VisitService visitService;
	
	@Override
	public boolean evaluate(Patient patient, EligibilityCriteria criteria) {
		if (patient == null || criteria == null || StringUtils.isBlank(criteria.getCondition())) {
			return false;
		}
		
		String condition = criteria.getCondition().trim();
		Matcher matcher = FIRST_VISIT_PATTERN.matcher(condition);
		
		if (matcher.find()) {
			boolean isFirstVisitExpected = Boolean.parseBoolean(matcher.group(1));
			boolean isFirstVisit = isPatientFirstVisit(patient);
			return isFirstVisitExpected == isFirstVisit;
		}
		
		return false;
	}
	
	@Override
	public boolean canHandle(EligibilityCriteria criteria) {
		return criteria != null && EligibilityCriteriaType.VISIT_TYPE.equals(criteria.getCriteriaType());
	}
	
	/**
	 * Determines if this is the patient's first visit
	 * 
	 * @param patient the patient to check
	 * @return true if this is the patient's first visit, false otherwise
	 */
	private boolean isPatientFirstVisit(Patient patient) {
		if (visitService == null) {
			visitService = Context.getVisitService();
		}
		
		// TODO: we need to check if would not be better to also use an encounterType to filter the visits
		List<Visit> patientVisits = visitService.getVisitsByPatient(patient);
		return patientVisits.size() <= 1;
	}
}
