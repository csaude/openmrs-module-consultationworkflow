package org.openmrs.module.consultationworkflow.api.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitTypeEvaluatorTest {
	
	@Mock
	private VisitService visitService;
	
	@InjectMocks
	private VisitTypeEvaluator evaluator;
	
	private Patient patient;
	
	private EligibilityCriteria criteria;
	
	@BeforeEach
	public void setup() {
		// Initialize test patient
		patient = new Patient();
		patient.setId(1);
		
		// Initialize test criteria
		criteria = new EligibilityCriteria();
		criteria.setCriteriaType(EligibilityCriteriaType.VISIT_TYPE);
	}
	
	@Test
	public void canHandle_ShouldReturnTrueForVisitTypeType() {
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
    public void evaluate_ShouldReturnTrueForFirstVisitConditionWithNoVisits() {
        // Given
        criteria.setCondition("firstVisit==true");

        // Mock visitService to return empty list (first visit)
        List<Visit> emptyVisits = new ArrayList<>();
        when(visitService.getVisitsByPatient(patient)).thenReturn(emptyVisits);

        // When
        boolean result = evaluator.evaluate(patient, criteria);

        // Then
        assertTrue(result);
        verify(visitService).getVisitsByPatient(patient);
    }
	
	@Test
    public void evaluate_ShouldReturnTrueForFirstVisitConditionWithOneVisit() {
        // Given
        criteria.setCondition("firstVisit==true");

        // Mock visitService to return one visit (still considered first visit)
        List<Visit> singleVisit = new ArrayList<>();
        singleVisit.add(new Visit());
        when(visitService.getVisitsByPatient(patient)).thenReturn(singleVisit);

        // When
        boolean result = evaluator.evaluate(patient, criteria);

        // Then
        assertTrue(result);
        verify(visitService).getVisitsByPatient(patient);
    }
	
	@Test
    public void evaluate_ShouldReturnFalseForFirstVisitConditionWithMultipleVisits() {
        // Given
        criteria.setCondition("firstVisit==true");

        // Mock visitService to return multiple visits (not first visit)
        List<Visit> multipleVisits = new ArrayList<>();
        multipleVisits.add(new Visit());
        multipleVisits.add(new Visit());
        when(visitService.getVisitsByPatient(patient)).thenReturn(multipleVisits);

        // When
        boolean result = evaluator.evaluate(patient, criteria);

        // Then
        assertFalse(result);
        verify(visitService).getVisitsByPatient(patient);
    }
	
	@Test
    public void evaluate_ShouldReturnFalseForFirstVisitFalseConditionWithNoVisits() {
        // Given
        criteria.setCondition("firstVisit==false");

        // Mock visitService to return empty list (first visit)
        List<Visit> emptyVisits = new ArrayList<>();
        when(visitService.getVisitsByPatient(patient)).thenReturn(emptyVisits);

        // When
        boolean result = evaluator.evaluate(patient, criteria);

        // Then
        assertFalse(result);
        verify(visitService).getVisitsByPatient(patient);
    }
	
	@Test
    public void evaluate_ShouldReturnTrueForFirstVisitFalseConditionWithMultipleVisits() {
        // Given
        criteria.setCondition("firstVisit==false");

        // Mock visitService to return multiple visits (not first visit)
        List<Visit> multipleVisits = new ArrayList<>();
        multipleVisits.add(new Visit());
        multipleVisits.add(new Visit());
        when(visitService.getVisitsByPatient(patient)).thenReturn(multipleVisits);

        // When
        boolean result = evaluator.evaluate(patient, criteria);

        // Then
        assertTrue(result);
        verify(visitService).getVisitsByPatient(patient);
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
    @Disabled // TODO: fix this, is throwing org.openmrs.api.ServiceNotFoundException: Service not found: interface org.openmrs.api.VisitService
    public void evaluate_ShouldHandleNullVisitService() {
        // Given
        evaluator = new VisitTypeEvaluator();  // No mocked service
        criteria.setCondition("firstVisit==true");

        // Mock Context for service lookup
        when(Context.getVisitService()).thenReturn(visitService);

        // Mock visitService to return empty list (first visit)
        List<Visit> emptyVisits = new ArrayList<>();
        when(visitService.getVisitsByPatient(patient)).thenReturn(emptyVisits);

        // When
        boolean result = evaluator.evaluate(patient, criteria);

        // Then
        assertTrue(result);
    }
}
