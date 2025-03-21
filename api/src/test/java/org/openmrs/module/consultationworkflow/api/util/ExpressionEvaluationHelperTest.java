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
        assertTrue(expressionHelper.evaluateBoolean("#allergies.containsAll(new java.util.ArrayList(T(java.util" +
                ".Arrays).asList('AL1','AL2')))", context));
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
	
	// New tests to cover more edge cases
	
	@Test
	public void convertToSpelExpression_ShouldHandleAllComparisonOperators() {
		// Test each operator to ensure they're properly handled
		assertEquals("#age == 18", expressionHelper.convertToSpelExpression("age == 18"));
		assertEquals("#age != 18", expressionHelper.convertToSpelExpression("age != 18"));
		assertEquals("#age > 18", expressionHelper.convertToSpelExpression("age > 18"));
		assertEquals("#age < 18", expressionHelper.convertToSpelExpression("age < 18"));
		assertEquals("#age >= 18", expressionHelper.convertToSpelExpression("age >= 18"));
		assertEquals("#age <= 18", expressionHelper.convertToSpelExpression("age <= 18"));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleWhitespaceVariations() {
		// No spaces
		assertEquals("#age >= 18", expressionHelper.convertToSpelExpression("age>=18"));
		
		// Extra spaces
		assertEquals("#age >= 18", expressionHelper.convertToSpelExpression("age  >=   18"));
		
		// Tabs
		assertEquals("#age >= 18", expressionHelper.convertToSpelExpression("age\t>=\t18"));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleComplexVariableNames() {
		// Variables with numbers
		assertEquals("#patient2 >= 18", expressionHelper.convertToSpelExpression("patient2 >= 18"));
		
		// Variables with underscores
		assertEquals("#patient_age >= 18", expressionHelper.convertToSpelExpression("patient_age >= 18"));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleCompoundExpressions() {
		// Multiple operators
		String complexCondition = "age >= 18 && weight <= 100";
		assertEquals("#age >= 18 && #weight <= 100", expressionHelper.convertToSpelExpression(complexCondition));
		
		// With parentheses
		String withParentheses = "(age >= 18) && (weight <= 100 || height > 160)";
		assertEquals("(#age >= 18) && (#weight <= 100 || #height > 160)",
		    expressionHelper.convertToSpelExpression(withParentheses));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleStringValues() {
		// Single quotes
		assertEquals("#name == 'John'", expressionHelper.convertToSpelExpression("name == 'John'"));
		
		// Double quotes
		assertEquals("#name == \"John\"", expressionHelper.convertToSpelExpression("name == \"John\""));
		
		// Empty string
		assertEquals("#name == ''", expressionHelper.convertToSpelExpression("name == ''"));
		
		// String with special characters
		assertEquals("#name == 'O\\'Neill'", expressionHelper.convertToSpelExpression("name == 'O\\'Neill'"));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleNumericValues() {
		// Integer
		assertEquals("#age >= 18", expressionHelper.convertToSpelExpression("age >= 18"));
		
		// Decimal
		assertEquals("#weight <= 72.5", expressionHelper.convertToSpelExpression("weight <= 72.5"));
		
		// Negative
		assertEquals("#temperature < -10", expressionHelper.convertToSpelExpression("temperature < -10"));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleArrayVariations() {
		// Empty array
		String emptyArray = "allergies == []";
		assertTrue(expressionHelper.convertToSpelExpression(emptyArray).contains(
		    "#allergies.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList()))"));
		
		// Single item
		String singleItem = "allergies == ['AL1']";
		assertTrue(expressionHelper.convertToSpelExpression(singleItem).contains(
		    "#allergies.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList('AL1')))"));
		
		// Mixed quotes
		String mixedQuotes = "allergies == ['AL1',\"AL2\"]";
		assertTrue(expressionHelper.convertToSpelExpression(mixedQuotes).contains(
		    "#allergies.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList('AL1',\"AL2\")))"));
		
		// With spaces
		String withSpaces = "allergies == ['AL 1', 'AL 2']";
		assertTrue(expressionHelper.convertToSpelExpression(withSpaces).contains(
		    "#allergies.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList('AL 1', 'AL 2'))" + ")"));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleReservedWords() {
		// Some expressions might contain words that could be confused with operators
		String withReservedWords = "greater == 10 && equals >= 5";
		assertEquals("#greater == 10 && #equals >= 5", expressionHelper.convertToSpelExpression(withReservedWords));
	}
	
	@Test
	public void convertToSpelExpression_ShouldHandleMultipleArrayComparisonPatterns() {
		// Testing multiple array comparison patterns in one expression
		String multipleArrays = "allergies == ['AL1'] && medications == ['MED1', 'MED2']";
		String result = expressionHelper.convertToSpelExpression(multipleArrays);
		
		assertTrue(result
		        .contains("#allergies.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList('AL1'))" + ")"));
		assertTrue(result.contains("#medications.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList"
		        + "('MED1', 'MED2')))"));
	}
}
