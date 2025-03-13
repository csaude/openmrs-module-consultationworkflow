package org.openmrs.module.consultationworkflow.api.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluator;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.openmrs.module.consultationworkflow.util.ExpressionEvaluationHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluator for PATIENT_ATTRIBUTES criteria
 */
@Component
@Slf4j
@AllArgsConstructor
public class PatientAttributesEvaluator implements EligibilityCriteriaEvaluator {
	
	private ExpressionEvaluationHelper expressionHelper;
	
	@Override
	public boolean evaluate(Patient patient, EligibilityCriteria criteria) {
		if (patient == null || criteria == null || StringUtils.isBlank(criteria.getCondition())) {
			return false;
		}
		
		// Extract patient attributes into a map for the SpEL context
		Map<String, Object> patientAttributes = extractPatientAttributes(patient);
		
		// Convert condition to SpEL expression
		// Handle conditions like: allergies == ['AL1','AL2'] && hivStatus == 'Positive'
		String condition = criteria.getCondition().trim();
		
		// Special handling for array conditions
		condition = preprocessArrayConditions(condition);
		
		String spelExpression = expressionHelper.convertToSpelExpression(condition);
		
		return expressionHelper.evaluateBoolean(spelExpression, patientAttributes);
	}
	
	@Override
	public boolean canHandle(EligibilityCriteria criteria) {
		return criteria != null && EligibilityCriteriaType.PATIENT_ATTRIBUTES.equals(criteria.getCriteriaType());
	}
	
	/**
	 * Preprocesses array conditions to make them SpEL compatible
	 * 
	 * @param condition the original condition
	 * @return processed condition
	 */
	String preprocessArrayConditions(String condition) {
		// Convert array equality checks to containsAll method calls
		// Example: allergies == ['AL1','AL2'] becomes allergies.containsAll(['AL1','AL2'])
		Pattern arrayPattern = Pattern.compile("(\\w+)\\s*==\\s*\\[(.*?)\\]");
		Matcher matcher = arrayPattern.matcher(condition);
		
		StringBuilder result = new StringBuilder();
		while (matcher.find()) {
			String attribute = matcher.group(1);
			String arrayValues = matcher.group(2);
			
			// Format into SpEL list containment check
			String replacement = attribute + ".containsAll([" + arrayValues + "])";
			matcher.appendReplacement(result, replacement);
		}
		matcher.appendTail(result);
		
		return result.toString();
	}
	
	/**
	 * Extracts patient attribute values
	 * 
	 * @param patient the patient
	 * @return map of attribute names to values
	 */
	private Map<String, Object> extractPatientAttributes(Patient patient) {
        Map<String, Object> attributes = new HashMap<>();

        for (PersonAttribute attribute : patient.getAttributes()) {
            if (attribute.getAttributeType() != null &&
                    attribute.getAttributeType().getName() != null &&
                    attribute.getValue() != null) {

                String name = attribute.getAttributeType().getName().replace(" ", "").toLowerCase();

                // Handle multiple values for the same attribute (like allergies)
                if (attributes.containsKey(name)) {
                    Object existingValue = attributes.get(name);
                    if (existingValue instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> list = (List<String>) existingValue;
                        list.add(attribute.getValue());
                    } else {
                        List<String> newList = new ArrayList<>();
                        newList.add(existingValue.toString());
                        newList.add(attribute.getValue());
                        attributes.put(name, newList);
                    }
                } else {
                    attributes.put(name, attribute.getValue());
                }
            }
        }

        return attributes;
    }
}
