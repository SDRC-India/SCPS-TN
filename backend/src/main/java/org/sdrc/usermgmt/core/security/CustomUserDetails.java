package org.sdrc.usermgmt.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author subham
 *
 */
@Component
public class CustomUserDetails implements UserDetailsService {

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired(required = false)
	private CustomMongoUserDetails customMongoUserDetails;

	@Autowired(required = false)
	private CustomJPAUserDetails customJPAUserDetails;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		String dataBaseType = configurableEnvironment.getProperty("app.datasource.type");

		switch (dataBaseType) {

			case "MONGO": {
				return customMongoUserDetails.loadUserByUsername(username);
			}
	
			case "SQL": {
				return customJPAUserDetails.loadUserByUsername(username);
			}
		}

		throw new RuntimeException(" property 'app.datasource.type' is invalid, only SQL and MONGO is requires!");
	}

}
