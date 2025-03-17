package org.openmrs.module.consultationworkflow.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Helper class for evaluating expressions using Spring Expression Language (SpEL)
 */
@Component
public class ExpressionEvaluationHelper {
	
	private final ExpressionParser parser = new SpelExpressionParser();
	
	/**
	 * Evaluates a SpEL expression against a context object
	 * 
	 * @param expressionString the expression to evaluate
	 * @param contextVariables map of variables to make available in the expression context
	 * @return the result of evaluation, or null if evaluation fails
	 */
	public Boolean evaluateBoolean(String expressionString, Map<String, Object> contextVariables) {
		try {
			// Prepare the evaluation context with variables
			EvaluationContext context = new StandardEvaluationContext();
			
			// Add each variable to the context
			for (Map.Entry<String, Object> entry : contextVariables.entrySet()) {
				context.setVariable(entry.getKey(), entry.getValue());
			}
			
			// Parse and evaluate the expression
			Expression expression = parser.parseExpression(expressionString);
			return expression.getValue(context, Boolean.class);
		}
		catch (ParseException e) {
			// Handle parsing errors
			return false;
		}
		catch (Exception e) {
			// Handle other evaluation errors
			return false;
		}
	}
	
	/**
	 * Converts an eligibility condition to a valid SpEL expression
	 * 
	 * @param condition the original condition string
	 * @return SpEL compatible expression string
	 */
	public String convertToSpelExpression(String condition) {
		// Replace common operators
		String spelExpression = condition;
		
		// Handle array comparisons like: allergies == ['AL1','AL2']
		spelExpression = spelExpression.replaceAll("(\\w+)\\s*==\\s*\\[(.*?)\\]",
				"#$1.containsAll(new java.util.ArrayList(T(java.util.Arrays).asList($2)))");
		
		// Replace variable references to use SpEL variable syntax
		spelExpression = spelExpression.replaceAll("(\\b\\w+\\b)\\s*(==|!=|>=|<=|>|<)\\s*", "#$1 $2 ");
		
		return spelExpression;
	}
}
