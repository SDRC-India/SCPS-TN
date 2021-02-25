package org.sdrc.JPAtokenstrore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;

/**
 * @author subham
 * 
 * This class is used to create TokenStore bean- which is used to
 *  store token in database
 *
 */
@ConditionalOnExpression("'${application.security.type}'=='oauth2-both' "
		+ "OR '${application.security.type}'=='oauth2-oauthserver'")
@Component
public class Oauth2AuthorizationServerTokenInitializer {

	@Autowired
	private javax.sql.DataSource dataSource;

	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

}