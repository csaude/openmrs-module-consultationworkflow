package org.openmrs.module.consultationworkflow.api.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.Patient;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.openmrs.module.consultationworkflow.util.ExpressionEvaluationHelper;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientDemographicsEvaluatorTest {
	
	@Mock
	private ExpressionEvaluationHelper expressionHelper;
	
	@InjectMocks
	private PatientDemographicsEvaluator evaluator;
	
	private Patient patient;
	
	private EligibilityCriteria criteria;
	
	@BeforeEach
	public void setup() {
		// Initialize test patient
		patient = new Patient();
		patient.setGender("M");
		
		// Set birthdate to make patient 25 years old
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -25);
		patient.setBirthdate(calendar.getTime());
		
		// Initialize test criteria
		criteria = new EligibilityCriteria();
		criteria.setCriteriaType(EligibilityCriteriaType.PATIENT_DEMOGRAPHICS);
	}
	
	@Test
	public void canHandle_ShouldReturnTrueForPatientDemographicsType() {
		// When
		boolean result = evaluator.canHandle(criteria);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void canHandle_ShouldReturnFalseForOtherTypes() {
		// Given
		criteria.setCriteriaType(EligibilityCriteriaType.PROGRAM);
		
		// When
		boolean result = evaluator.canHandle(criteria);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void canHandle_ShouldReturnFalseForNullCriteria() {
		// When
		boolean result = evaluator.canHandle(null);
		
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
	public void evaluate_ShouldUseExpressionHelperForEvaluation() {
		// Given
		String condition = "age > 18 && gender == 'M'";
		criteria.setCondition(condition);
		
		// Mock the expression helper
		when(expressionHelper.convertToSpelExpression(anyString())).thenReturn("#age > 18 && #gender == 'M'");
		when(expressionHelper.evaluateBoolean(anyString(), anyMap())).thenReturn(true);
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
		verify(expressionHelper).convertToSpelExpression(condition);
		verify(expressionHelper).evaluateBoolean(anyString(), anyMap());
	}
	
	@Test
	public void evaluate_ShouldExtractCorrectPatientValues() {
		// Given
		String condition = "age > 18 && gender == 'M'";
		criteria.setCondition(condition);
		
		// Use a real expression helper to test the extraction logic
		evaluator = new PatientDemographicsEvaluator(new ExpressionEvaluationHelper());
		expressionHelper = new ExpressionEvaluationHelper();
		
		// Use reflection to inject the expression helper
		try {
			java.lang.reflect.Field field = PatientDemographicsEvaluator.class.getDeclaredField("expressionHelper");
			field.setAccessible(true);
			field.set(evaluator, expressionHelper);
		}
		catch (Exception e) {
			fail("Failed to inject expressionHelper: " + e.getMessage());
		}
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void evaluate_ShouldReturnFalseForInvalidCondition() {
		// Given
		String condition = "age > 30";
		criteria.setCondition(condition);
		
		// Use the real expression helper to test the evaluation
		evaluator = new PatientDemographicsEvaluator(new ExpressionEvaluationHelper());
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	// should return true for correct condition
	public void evaluate_ShouldReturnTrueForCorrectCondition() {
		// Given
		String condition = "age >= 18";
		criteria.setCondition(condition);
		
		// Use the real expression helper to test the evaluation
		evaluator = new PatientDemographicsEvaluator(new ExpressionEvaluationHelper());
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
	}
}
