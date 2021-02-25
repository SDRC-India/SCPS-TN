package org.sdrc.usermgmt.core.util;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * @author Subham Ashish (subham@sdrc.co.in)
 * 
 *Added to activate basic authentication.
 *it reads value of "application.security.type" if matches 'basic or both' than
 *basic authentication security activates in the application.
 */
public class BasicAuthenticationPropertyCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

		String securityType = context.getEnvironment().getProperty("application.security.type");

		return securityType == null ? false : securityType.equalsIgnoreCase("basic") || securityType.equalsIgnoreCase("both");
	}
}
