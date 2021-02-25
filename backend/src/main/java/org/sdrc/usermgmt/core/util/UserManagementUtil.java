package org.sdrc.usermgmt.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Subham Ashish
 * 
 *         This class loads 'ugmtmessages.properties' in spring context
 * 
 *         It also check whether necessary properties are presnt in
 *         application.properties or not
 *
 */
@Component
@PropertySource(value = { "classpath:ugmtmessages.properties" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class UserManagementUtil {

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * It runs on container startup and check all the required property for
	 * sending mail is present in application.properties
	 * 
	 */
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!configurableEnvironment.containsProperty("authentication.userid")) {
			throw new RuntimeException(
					"property authentication.userid is not set in application.properties ie used for sending mail");
		}
		if (!configurableEnvironment.containsProperty("authentication.password")) {
			throw new RuntimeException(
					"property authentication.password is not set in application.properties ie used for sending mail");

		}

		if (configurableEnvironment.containsProperty("allow.admin.creation")) {
			if (!(configurableEnvironment.getProperty("allow.admin.creation").equals("false")
					|| configurableEnvironment.getProperty("allow.admin.creation").equals("true"))) {
				throw new RuntimeException(
						"property 'allow.admin.creation' is set to undefined value it can only accept true or false");
			}

		}

	}
}
