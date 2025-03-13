package org.openmrs.module.consultationworkflow.api.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.consultationworkflow.util.ExpressionEvaluationHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionEvaluationHelperTest {
	
	private ExpressionEvaluationHelper expressionHelper;
	
	@BeforeEach
	public void setup() {
		expressionHelper = new ExpressionEvaluationHelper();
	}
	
	@Test
    public void evaluateBoolean_ShouldEvaluateSimpleExpressions() {
        // Given
        Map<String, Object> context = new HashMap<>();
        context.put("age", 25);
        context.put("gender", "M");

        // When - Then
        assertTrue(expressionHelper.evaluateBoolean("#age > 18", context));
        assertTrue(expressionHelper.evaluateBoolean("#gender == 'M'", context));
        assertFalse(expressionHelper.evaluateBoolean("#age < 20", context));
        assertTrue(expressionHelper.evaluateBoolean("#age > 18 && #gender == 'M'", context));
        assertFalse(expressionHelper.evaluateBoolean("#age > 30 || #gender == 'F'", context));
    }
	
	@Test
    public void evaluateBoolean_ShouldHandleNullValues() {
        // Given
        Map<String, Object> context = new HashMap<>();
        context.put("age", null);
        context.put("gender", "M");

        // When - Then
        assertFalse(expressionHelper.evaluateBoolean("#age > 18", context));
        assertTrue(expressionHelper.evaluateBoolean("#gender == 'M'", context));
    }
	
	@Test
    public void evaluateBoolean_ShouldHandleCollections() {
        // Given
        Map<String, Object> context = new HashMap<>();
        List<String> allergies = Arrays.asList("AL1", "AL2", "AL3");
        context.put("allergies", allergies);

        // When - Then
        assertTrue(expressionHelper.evaluateBoolean("#allergies.contains('AL1')", context));
        assertTrue(expressionHelper.evaluateBoolean("#allergies.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList('AL1','AL2')))", context));
        assertFalse(expressionHelper.evaluateBoolean("#allergies.contains('AL4')", context));
    }
	
	@Test
    public void evaluateBoolean_ShouldHandleInvalidExpressions() {
        // Given
        Map<String, Object> context = new HashMap<>();
        context.put("age", 25);

        // When - Then
        assertFalse(expressionHelper.evaluateBoolean("invalid expression", context));
        assertFalse(expressionHelper.evaluateBoolean("age >< 25", context));
    }
	
	@Test
	public void convertToSpelExpression_ShouldHandleSimpleConditions() {
		// Given
		String condition = "age > 18 && gender == male";
		
		// When
		String spelExpression = expressionHelper.convertToSpelExpression(condition);
		
		// Then
		assertEquals("#age > 18 && #gender == male", spelExpression);
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleArrayConditions() {
		// Given
		String condition = "allergies == ['AL1','AL2']";
		
		// When
		String spelExpression = expressionHelper.convertToSpelExpression(condition);
		
		// Then
		assertTrue(spelExpression
		        .contains("#allergies.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList('AL1','AL2')))"));
	}
}
