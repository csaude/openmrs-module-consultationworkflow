package org.openmrs.module.consultationworkflow.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluator;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluator for PROGRAM type criteria
 */
@Component
public class ProgramEvaluator implements EligibilityCriteriaEvaluator {
	
	// Pattern to match conditions like: program==uuid-xxxx
	private static final Pattern PROGRAM_PATTERN = Pattern.compile("program\\s*==\\s*([\\w-]+)");
	
	private ProgramWorkflowService programWorkflowService;
	
	@Override
	public boolean evaluate(Patient patient, EligibilityCriteria criteria) {
		if (patient == null || criteria == null || StringUtils.isBlank(criteria.getCondition())) {
			return false;
		}
		
		String condition = criteria.getCondition().trim();
		Matcher matcher = PROGRAM_PATTERN.matcher(condition);
		
		if (matcher.find()) {
			String programUuid = matcher.group(1);
			return isPatientEnrolledInProgram(patient, programUuid);
		}
		
		return false;
	}
	
	@Override
	public boolean canHandle(EligibilityCriteria criteria) {
		return criteria != null && EligibilityCriteriaType.PROGRAM.equals(criteria.getCriteriaType());
	}
	
	/**
	 * Checks if a patient is enrolled in a program with the given UUID
	 * 
	 * @param patient the patient to check
	 * @param programUuid the program UUID
	 * @return true if the patient is enrolled, false otherwise
	 */
	private boolean isPatientEnrolledInProgram(Patient patient, String programUuid) {
        if (programWorkflowService == null) {
            programWorkflowService = Context.getProgramWorkflowService();
        }

        List<PatientProgram> patientPrograms = programWorkflowService.getPatientPrograms(
                patient, null, null, null, null, null, false);

        return patientPrograms.stream()
                .anyMatch(program ->
                        program.getProgram() != null &&
                                programUuid.equals(program.getProgram().getUuid()) &&
                                !program.getVoided() &&
                                program.getDateEnrolled() != null &&
                                program.getDateCompleted() == null);
    }
}
