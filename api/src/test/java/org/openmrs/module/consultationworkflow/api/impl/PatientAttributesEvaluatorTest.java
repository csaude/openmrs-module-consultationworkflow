package org.openmrs.module.consultationworkflow.api.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.openmrs.module.consultationworkflow.util.ExpressionEvaluationHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientAttributesEvaluatorTest {
	
	@Mock
	private ExpressionEvaluationHelper expressionHelper;
	
	@InjectMocks
	private PatientAttributesEvaluator evaluator;
	
	private Patient patient;
	
	private EligibilityCriteria criteria;
	
	@BeforeEach
	public void setup() {
		// Initialize test patient with attributes
		patient = new Patient();
		
		// Add HIV Status attribute
		PersonAttributeType hivStatusType = new PersonAttributeType();
		hivStatusType.setName("HIV Status");
		PersonAttribute hivStatus = new PersonAttribute();
		hivStatus.setAttributeType(hivStatusType);
		hivStatus.setValue("Positive");
		patient.addAttribute(hivStatus);
		
		// Add Allergies attributes
		PersonAttributeType allergiesType = new PersonAttributeType();
		allergiesType.setName("Allergies");
		
		PersonAttribute allergy1 = new PersonAttribute();
		allergy1.setAttributeType(allergiesType);
		allergy1.setValue("AL1");
		patient.addAttribute(allergy1);
		
		PersonAttribute allergy2 = new PersonAttribute();
		allergy2.setAttributeType(allergiesType);
		allergy2.setValue("AL2");
		patient.addAttribute(allergy2);
		
		// Initialize test criteria
		criteria = new EligibilityCriteria();
		criteria.setCriteriaType(EligibilityCriteriaType.PATIENT_ATTRIBUTES);
	}
	
	@Test
	public void canHandle_ShouldReturnTrueForPatientAttributesType() {
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
	public void evaluate_ShouldHandleSimpleCondition() {
		// Given
		String condition = "hivStatus == 'Positive'";
		criteria.setCondition(condition);
		
		// Mock the expression helper
		when(expressionHelper.convertToSpelExpression(anyString())).thenReturn("#hivStatus == 'Positive'");
		when(expressionHelper.evaluateBoolean(anyString(), anyMap())).thenReturn(true);
		
		// When
		boolean result = evaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
		verify(expressionHelper).convertToSpelExpression(anyString());
		verify(expressionHelper).evaluateBoolean(anyString(), anyMap());
	}
	
	@Test
	public void evaluate_ShouldHandleArrayCondition() {
		// Given
		String condition = "allergies == ['AL1','AL2']";
		criteria.setCondition(condition);
		
		// Mock preprocessArrayConditions method using spy
		PatientAttributesEvaluator spyEvaluator = spy(evaluator);
		doReturn("allergies.containsAll(['AL1','AL2'])").when(spyEvaluator).preprocessArrayConditions(anyString());
		
		// Mock the expression helper
		when(expressionHelper.convertToSpelExpression(anyString())).thenReturn("#allergies.containsAll(['AL1','AL2'])");
		when(expressionHelper.evaluateBoolean(anyString(), anyMap())).thenReturn(true);
		
		// When
		boolean result = spyEvaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
		verify(spyEvaluator).preprocessArrayConditions(condition);
		verify(expressionHelper).convertToSpelExpression(anyString());
		verify(expressionHelper).evaluateBoolean(anyString(), anyMap());
	}
	
	@Test
	public void evaluate_ShouldHandleComplexConditionWithLogicalOperators() {
		// Given
		String condition = "hivStatus == 'Positive' && allergies == ['AL1','AL2']";
		criteria.setCondition(condition);
		
		// Mock preprocessArrayConditions method using spy
		PatientAttributesEvaluator spyEvaluator = spy(evaluator);
		doReturn("hivStatus == 'Positive' && allergies.containsAll(['AL1','AL2'])").when(spyEvaluator)
		        .preprocessArrayConditions(anyString());
		
		// Mock the expression helper
		when(expressionHelper.convertToSpelExpression(anyString())).thenReturn(
		    "#hivStatus == 'Positive' && #allergies.containsAll(['AL1','AL2'])");
		when(expressionHelper.evaluateBoolean(anyString(), anyMap())).thenReturn(true);
		
		// When
		boolean result = spyEvaluator.evaluate(patient, criteria);
		
		// Then
		assertTrue(result);
		verify(spyEvaluator).preprocessArrayConditions(condition);
		verify(expressionHelper).convertToSpelExpression(anyString());
		verify(expressionHelper).evaluateBoolean(anyString(), anyMap());
	}
}
