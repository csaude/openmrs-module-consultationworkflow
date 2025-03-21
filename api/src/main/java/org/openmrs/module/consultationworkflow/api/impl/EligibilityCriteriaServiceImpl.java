package org.openmrs.module.consultationworkflow.api.impl;

import lombok.AllArgsConstructor;
import org.openmrs.Patient;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluator;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluatorFactory;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaService;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the EligibilityCriteriaService
 */
@Service
@AllArgsConstructor
public class EligibilityCriteriaServiceImpl implements EligibilityCriteriaService {
	
	private EligibilityCriteriaEvaluatorFactory evaluatorFactory;
	
	@Override
	public boolean isPatientEligible(Patient patient, EligibilityCriteria criteria) {
		if (patient == null || criteria == null) {
			return false;
		}
		
		EligibilityCriteriaEvaluator evaluator = evaluatorFactory.getEvaluator(criteria);
		
		if (evaluator == null) {
			return false;
		}
		
		return evaluator.evaluate(patient, criteria);
	}
	
	@Override
	public boolean isPatientEligible(Patient patient, List<EligibilityCriteria> criteriaList) {
		if (patient == null || criteriaList == null || criteriaList.isEmpty()) {
			return false;
		}
		
		// Patient must satisfy all criteria to be eligible
		for (EligibilityCriteria criteria : criteriaList) {
			if (!isPatientEligible(patient, criteria)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
    public List<EligibilityCriteria> getMatchingCriteria(Patient patient, List<EligibilityCriteria> criteriaList) {
        List<EligibilityCriteria> matchingCriteria = new ArrayList<>();

        if (patient == null || criteriaList == null) {
            return matchingCriteria;
        }

        for (EligibilityCriteria criteria : criteriaList) {
            if (isPatientEligible(patient, criteria)) {
                matchingCriteria.add(criteria);
            }
        }

        return matchingCriteria;
    }
}
