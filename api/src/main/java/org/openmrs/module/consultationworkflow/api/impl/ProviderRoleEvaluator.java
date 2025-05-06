package org.openmrs.module.consultationworkflow.api.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluator;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProviderRoleEvaluator implements EligibilityCriteriaEvaluator {
	
	@Override
    public boolean evaluate(Patient patient, EligibilityCriteria criteria) {
        if(criteria == null || criteria.getCondition() == null) {
            return false;
        }

        String condition = criteria.getCondition().trim().split("==")[1].trim();

        log.info("Evaluating provider role condition: {}", condition);

        return Context.getAuthenticatedUser().getRoles()
                .stream()
                .anyMatch(role -> role.getUuid().equalsIgnoreCase(condition));
    }
	
	@Override
	public boolean canHandle(EligibilityCriteria criteria) {
		return criteria != null && criteria.getCriteriaType() != null
		        && criteria.getCriteriaType().equals(EligibilityCriteriaType.PROVIDER_ROLE);
	}
}
