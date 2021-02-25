package org.sdrc.usermgmt.core.security;

import javax.sql.DataSource;

import org.sdrc.mongoclientdetails.MongoClientDetailsService;
import org.sdrc.mongotokenstrore.MongoTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/*
 * @author Subham Ashish(subham@sdrc.co.in)
 * 
 * @author azar(azaruddin@sdrc.co.in)
 * 
 * This class gets active if and only if
 * @ConditionalOnExpression TRUE
 * it activates oauth2 and JWT authorization server configuration
 * 
 */
@Configuration
@EnableAuthorizationServer
@ConditionalOnExpression("'${application.security.type}'=='jwt-both' OR "
		+ "'${application.security.type}'=='oauth2-both' OR " 
		+ "'${application.security.type}'=='oauth2-oauthserver' "
		+ "OR '${application.security.type}'=='jwt-oauthserver'")
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

	@Autowired(required = false)
	private DataSource dataSource;

	@Autowired
	private UserAuthenticationProvider userAuthenticationProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetails customUserDetails;

	@Autowired(required = false)
	private JwtAccessTokenConverter accessTokenConverter;

	@Autowired(required = false)
	private TokenStore tokenStore;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired(required = false)
	private MongoTokenStore mongoTokenStore;
	
	@Autowired(required = false)
	private MongoClientDetailsService mongoClientDetailsService;
	
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

		if (configurableEnvironment.getProperty("application.security.type").equals("oauth2-oauthserver")
				|| configurableEnvironment.getProperty("application.security.type").equals("oauth2-both")) {

			// checking database type
			String databaseType = configurableEnvironment.getProperty("app.datasource.type");
			switch (databaseType) {
			case "SQL": {
				endpoints.tokenStore(tokenStore).authenticationManager(authenticationManager)
						.userDetailsService(customUserDetails);// added to get new access token with refresh token
			}
				break;

			case "MONGO": {
				endpoints.tokenStore(mongoTokenStore).authenticationManager(authenticationManager)
						.userDetailsService(customUserDetails);//added to get new access token with refresh token
			}
				break;
			}

		} else {

			endpoints.tokenStore(tokenStore).authenticationManager(authenticationManager)
			.userDetailsService(customUserDetails) // added to get new access token with refresh token
			.tokenServices(tokenServices());

		}
	}

	@Bean
	@Primary
	@ConditionalOnExpression("'${application.security.type}'=='jwt-both' "
			+ "OR '${application.security.type}'=='jwt-oauthserver'")
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore);
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setTokenEnhancer(accessTokenConverter);
		
		//if only mongotokestore enables
		if(configurableEnvironment.getProperty("app.datasource.type").equals("MONGO")){
			defaultTokenServices.setClientDetailsService(mongoClientDetailsService);
		}else{
			//default clientdetails for SQL
			defaultTokenServices.setClientDetailsService(clientDetailsService);
		}
			
		return defaultTokenServices;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.checkTokenAccess("permitAll()");
		/*
		 * added to call custom filter before BasicAuthenticationFilter
		 */
		security.addTokenEndpointAuthenticationFilter(new RequestLoggingInterceptor());
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		String dataBaseType = configurableEnvironment.getProperty("app.datasource.type");

		if (dataBaseType.equals("SQL")) {
			clients.jdbc(dataSource);
		} else {
			clients.withClientDetails(mongoClientDetailsService);
		}

	}

	@Autowired
	public void authenticationManager(AuthenticationManagerBuilder builder) throws Exception {
		builder.authenticationProvider(userAuthenticationProvider);
	}

}
