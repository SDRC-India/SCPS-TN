package org.sdrc.usermgmt.core.util;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AuthorizationServerPropertyCondition implements Condition{

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

		String securityType = context.getEnvironment().getProperty("application.security.type");

		return securityType == null ? false
				: securityType.equalsIgnoreCase("jwt-both") || securityType.equalsIgnoreCase("jwt-both,basic");
	}
}
