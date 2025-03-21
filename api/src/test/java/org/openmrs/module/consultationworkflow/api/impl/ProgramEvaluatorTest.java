package org.openmrs.module.consultationworkflow.api.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProgramEvaluatorTest {
	
	@Mock
	private ProgramWorkflowService programWorkflowService;
	
	@InjectMocks
	private ProgramEvaluator evaluator;
	
	private Patient patient;
	
	private EligibilityCriteria criteria;
	
	private List<PatientProgram> patientPrograms;
	
	@BeforeEach
    public void setup() {
        // Initialize test patient
        patient = new Patient();
        patient.setId(1);

        // Initialize test criteria
        criteria = new EligibilityCriteria();
        criteria.setCriteriaType(EligibilityCriteriaType.PROGRAM);

        // Initialize patient programs
        patientPrograms = new ArrayList<>();

        // Create an active program
        Program program1 = new Program();
        program1.setUuid("program-uuid-1");
        PatientProgram patientProgram1 = new PatientProgram();
        patientProgram1.setProgram(program1);
        patientProgram1.setVoided(false);
        patientProgram1.setDateEnrolled(new Date());
        patientPrograms.add(patientProgram1);

        // Create another active program
        Program program2 = new Program();
        program2.setUuid("program-uuid-2");
        PatientProgram patientProgram2 = new PatientProgram();
        patientProgram2.setProgram(program2);
        patientProgram2.setVoided(false);
        patientProgram2.setDateEnrolled(new Date());
        patientPrograms.add(patientProgram2);

    }
	
	@Test
	public void canHandle_ShouldReturnTrueForProgramType() {
		// When
		boolean result = evaluator.canHandle(criteria);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void canHandle_ShouldReturnFalseForOtherTypes() {
		// Given
		criteria.setCriteriaType(EligibilityCriteriaType.PATIENT_DEMOGRAPHICS);
		
		// When
		boolean result = evaluator.canHandle(criteria);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void evaluate_ShouldReturnFalseForNullPatient() {
		// When
		boolean result = evaluator.evaluate(null, criteria);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void evaluate_ShouldReturnFalseForNullCriteria() {
		// When
		boolean result = evaluator.evaluate(patient, null);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void evaluate_ShouldReturnFalseForEmptyCondition() {
		// Given
		criteria.setCondition("");
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	@Disabled
	public void evaluate_ShouldReturnTrueForMatchingProgram() {
		// Given
		criteria.setCondition("program==program-uuid-1");
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
		verify(programWorkflowService).getPatientPrograms(eq(patient), any(), any(), any(), any(), any(), eq(false));
	}
	
	@Test
	public void evaluate_ShouldReturnFalseForNonMatchingProgram() {
		// Given
		criteria.setCondition("program==program-uuid-3");
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertFalse(result);
		verify(programWorkflowService).getPatientPrograms(eq(patient), any(), any(), any(), any(), any(), eq(false));
	}
	
	@Test
	public void evaluate_ShouldHandleInvalidConditionFormat() {
		// Given
		criteria.setCondition("invalid-format");
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	@Disabled
	public void evaluate_ShouldHandleNullProgramWorkflowService() {
		// Given
		evaluator = new ProgramEvaluator(); // No mocked service
		criteria.setCondition("program==program-uuid-1");
		
		when(Context.getProgramWorkflowService()).thenReturn(programWorkflowService);
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
	}
}
