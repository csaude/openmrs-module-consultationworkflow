package org.openmrs.module.consultationworkflow.api;

import lombok.AllArgsConstructor;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Factory for getting the appropriate evaluator for a given criteria
 */
@Component
@AllArgsConstructor
public class EligibilityCriteriaEvaluatorFactory {
	
	private Set<EligibilityCriteriaEvaluator> evaluators;
	
	/**
	 * Gets the appropriate evaluator for the given criteria
	 * 
	 * @param criteria the criteria to find an evaluator for
	 * @return the appropriate evaluator, or null if none found
	 */
	public EligibilityCriteriaEvaluator getEvaluator(EligibilityCriteria criteria) {
		if (criteria == null) {
			return null;
		}
		
		for (EligibilityCriteriaEvaluator evaluator : evaluators) {
			if (evaluator.canHandle(criteria)) {
				return evaluator;
			}
		}
		
		return null;
	}
	
	public void setEvaluators(List<EligibilityCriteriaEvaluator> evaluators) {
		this.evaluators = Set.copyOf(evaluators);
	}
	
}
