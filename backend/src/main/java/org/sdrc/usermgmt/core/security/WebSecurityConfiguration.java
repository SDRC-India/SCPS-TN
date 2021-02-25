package org.sdrc.usermgmt.core.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.sdrc.usermgmt.core.util.BasicAuthenticationPropertyCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author subham It activate Basic Authentication security, if and only if
 *         - @Conditional TRUE
 */
@Configuration
@Order(1)
@EnableWebSecurity
@Conditional(BasicAuthenticationPropertyCondition.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserAuthenticationProvider userAuthenticationProvider;

	@Autowired
	private void configureGlobal(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(userAuthenticationProvider);
	}

	/*
	 * To alter any configuration related to WEB-Application please update the
	 * configuration.
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {

		// .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

		http.logout().logoutUrl("/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID", "XSRF-TOKEN");
		http.sessionManagement().sessionFixation().newSession();
		http.sessionManagement().enableSessionUrlRewriting(false); // disable url rewriting,to pass session information in URL.
																	

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = getClass().getClassLoader().getResourceAsStream("sdrc-security.properties");

			if (input != null) {
				/**
				 * if file enables and
				 * sdrc.security.disable.createuser.endpoints = true than bypass
				 * endpoints from security layer
				 */
				prop.load(input);// load a properties file from class path

				if (prop.getProperty("sdrc.security.disable.createuser.endpoints")!=null && prop.getProperty("sdrc.security.disable.createuser.endpoints").equals("true")) {

					http.httpBasic().and().authorizeRequests()
							.antMatchers("/createUser", "/getAllDesignations", "/getAllAuthorities").permitAll();

				} else {
					http.authorizeRequests().antMatchers("/createUser", "/getAllDesignations", "/getAllAuthorities")
							.access("hasAnyAuthority('USER_MGMT_ALL_API', 'CREATE_USER')");
				}

				/**
				 * for update user end point
				 */

				if (prop.getProperty("sdrc.security.disable.updateUser.endpoint")!=null && prop.getProperty("sdrc.security.disable.updateUser.endpoint").equals("true")) {

					http.httpBasic().and().authorizeRequests().antMatchers("/updateUser").permitAll();

				} else {
					http.authorizeRequests().antMatchers("/updateUser")
							.access("hasAnyAuthority('USER_MGMT_ALL_API', 'UPDATE_USER')");
				}
				
				String property = prop.getProperty("sdrc.security.basic.allow.path");
				if(property!=null) {
					String[] paths = property.split(",");
					for(String path : paths) {
						http.httpBasic().and().authorizeRequests().antMatchers(path).permitAll();
					}
				}

			}
			// if file doesnt exist than enable method level security
			else {
				http.authorizeRequests().antMatchers("/createUser", "/getAllDesignations", "/getAllAuthorities")
						.access("hasAnyAuthority('USER_MGMT_ALL_API', 'CREATE_USER')");
				
				http.authorizeRequests().antMatchers("/updateUser")
						.access("hasAnyAuthority('USER_MGMT_ALL_API', 'UPDATE_USER')");
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		http.httpBasic().and().authorizeRequests().antMatchers("/bypass/**").permitAll().anyRequest().authenticated()
				.and().csrf().disable();

	}

}