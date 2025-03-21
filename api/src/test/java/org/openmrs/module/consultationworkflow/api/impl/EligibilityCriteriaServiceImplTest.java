package org.openmrs.module.consultationworkflow.api.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.Patient;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluator;
import org.openmrs.module.consultationworkflow.api.EligibilityCriteriaEvaluatorFactory;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EligibilityCriteriaServiceImplTest {
	
	@Mock
	private EligibilityCriteriaEvaluatorFactory evaluatorFactory;
	
	@Mock
	private EligibilityCriteriaEvaluator mockEvaluator;
	
	@InjectMocks
	private EligibilityCriteriaServiceImpl service;
	
	private Patient patient;
	
	private EligibilityCriteria criteria1;
	
	private EligibilityCriteria criteria2;
	
	private EligibilityCriteria criteria3;
	
	private List<EligibilityCriteria> criteriaList;
	
	@BeforeEach
	public void setup() {
		// Initialize test patient
		patient = new Patient();
		patient.setId(1);
		
		// Initialize test criteria
		criteria1 = new EligibilityCriteria();
		criteria1.setId(1);
		criteria1.setCriteriaType(EligibilityCriteriaType.PATIENT_DEMOGRAPHICS);
		criteria1.setCondition("age > 18");
		
		criteria2 = new EligibilityCriteria();
		criteria2.setId(2);
		criteria2.setCriteriaType(EligibilityCriteriaType.PATIENT_ATTRIBUTES);
		criteria2.setCondition("hivStatus == 'Positive'");
		
		criteria3 = new EligibilityCriteria();
		criteria3.setId(3);
		criteria3.setCriteriaType(EligibilityCriteriaType.PROGRAM);
		criteria3.setCondition("program==program-uuid-1");
		
		criteriaList = Arrays.asList(criteria1, criteria2, criteria3);
	}
	
	@Test
	public void isPatientEligible_SingleCriteria_ShouldReturnFalseForNullPatient() {
		// When
		boolean result = service.isPatientEligible(null, criteria1);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void isPatientEligible_SingleCriteria_ShouldReturnFalseForNullCriteria() {
		// When
		boolean result = service.isPatientEligible(patient, (EligibilityCriteria) null);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void isPatientEligible_SingleCriteria_ShouldReturnFalseWhenNoEvaluatorFound() {
		// Given
		when(evaluatorFactory.getEvaluator(criteria1)).thenReturn(null);
		
		// When
		boolean result = service.isPatientEligible(patient, criteria1);
		
		// Then
		assertFalse(result);
		verify(evaluatorFactory).getEvaluator(criteria1);
	}
	
	@Test
	public void isPatientEligible_SingleCriteria_ShouldReturnEvaluatorResult() {
		// Given
		when(evaluatorFactory.getEvaluator(criteria1)).thenReturn(mockEvaluator);
		when(mockEvaluator.evaluate(patient, criteria1)).thenReturn(true);
		
		// When
		boolean result = service.isPatientEligible(patient, criteria1);
		
		// Then
		assertTrue(result);
		verify(evaluatorFactory).getEvaluator(criteria1);
		verify(mockEvaluator).evaluate(patient, criteria1);
	}
	
	@Test
	public void isPatientEligible_CriteriaList_ShouldReturnFalseForNullPatient() {
		// When
		boolean result = service.isPatientEligible(null, criteriaList);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void isPatientEligible_CriteriaList_ShouldReturnFalseForNullList() {
		// When
		boolean result = service.isPatientEligible(patient, (List<EligibilityCriteria>) null);
		
		// Then
		assertFalse(result);
	}
	
	@Test
    public void isPatientEligible_CriteriaList_ShouldReturnFalseForEmptyList() {
        // When
        boolean result = service.isPatientEligible(patient, new ArrayList<>());

        // Then
        assertFalse(result);
    }
	
	@Test
	public void isPatientEligible_CriteriaList_ShouldReturnTrueWhenAllCriteriaMet() {
		// Given
		when(evaluatorFactory.getEvaluator(any())).thenReturn(mockEvaluator);
		when(mockEvaluator.evaluate(eq(patient), any())).thenReturn(true);
		
		// When
		boolean result = service.isPatientEligible(patient, criteriaList);
		
		// Then
		assertTrue(result);
		verify(evaluatorFactory, times(3)).getEvaluator(any());
		verify(mockEvaluator, times(3)).evaluate(eq(patient), any());
	}
	
	@Test
	public void isPatientEligible_CriteriaList_ShouldReturnFalseWhenOneCriteriaNotMet() {
		// Given
		when(evaluatorFactory.getEvaluator(any())).thenReturn(mockEvaluator);
		
		// First two criteria are met, third is not
		when(mockEvaluator.evaluate(eq(patient), eq(criteria1))).thenReturn(true);
		when(mockEvaluator.evaluate(eq(patient), eq(criteria2))).thenReturn(true);
		when(mockEvaluator.evaluate(eq(patient), eq(criteria3))).thenReturn(false);
		
		// When
		boolean result = service.isPatientEligible(patient, criteriaList);
		
		// Then
		assertFalse(result);
		verify(evaluatorFactory, times(3)).getEvaluator(any());
		verify(mockEvaluator, times(3)).evaluate(eq(patient), any());
	}
	
	@Test
	public void getMatchingCriteria_ShouldReturnEmptyListForNullPatient() {
		// When
		List<EligibilityCriteria> result = service.getMatchingCriteria(null, criteriaList);
		
		// Then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getMatchingCriteria_ShouldReturnEmptyListForNullCriteriaList() {
		// When
		List<EligibilityCriteria> result = service.getMatchingCriteria(patient, null);
		
		// Then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getMatchingCriteria_ShouldReturnOnlyMatchingCriteria() {
		// Given
		when(evaluatorFactory.getEvaluator(any())).thenReturn(mockEvaluator);
		
		// First and third criteria are met, second is not
		when(mockEvaluator.evaluate(eq(patient), eq(criteria1))).thenReturn(true);
		when(mockEvaluator.evaluate(eq(patient), eq(criteria2))).thenReturn(false);
		when(mockEvaluator.evaluate(eq(patient), eq(criteria3))).thenReturn(true);
		
		// When
		List<EligibilityCriteria> result = service.getMatchingCriteria(patient, criteriaList);
		
		// Then
		assertEquals(2, result.size());
		assertTrue(result.contains(criteria1));
		assertFalse(result.contains(criteria2));
		assertTrue(result.contains(criteria3));
		
		verify(evaluatorFactory, times(3)).getEvaluator(any());
		verify(mockEvaluator, times(3)).evaluate(eq(patient), any());
	}
	
	@Test
	public void getMatchingCriteria_ShouldHandleNoMatchingCriteria() {
		// Given
		when(evaluatorFactory.getEvaluator(any())).thenReturn(mockEvaluator);
		when(mockEvaluator.evaluate(eq(patient), any())).thenReturn(false);
		
		// When
		List<EligibilityCriteria> result = service.getMatchingCriteria(patient, criteriaList);
		
		// Then
		assertTrue(result.isEmpty());
		verify(evaluatorFactory, times(3)).getEvaluator(any());
		verify(mockEvaluator, times(3)).evaluate(eq(patient), any());
	}
}
