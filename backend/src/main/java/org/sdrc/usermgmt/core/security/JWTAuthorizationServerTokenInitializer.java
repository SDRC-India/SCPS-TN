package org.sdrc.usermgmt.core.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.ResourceUtils;

/*
 * @author azar 
 * 
 * @auhor subham
 * 
 * This class initialize the token,
 * read .jks file from class path and load it in EnhancedJWTAccessTokenConverter
 * 
 */

@Configuration
@ConditionalOnExpression("'${application.security.type}'=='jwt-both' "
		+ "OR '${application.security.type}'=='jwt-oauthserver' OR '${application.security.type}'=='jwt-resserver'")
public class JWTAuthorizationServerTokenInitializer {

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {

		if (configurableEnvironment.getProperty("application.security.type").equals("jwt-oauthserver")) {

			EnhancedJWTAccessTokenConverter converter = new EnhancedJWTAccessTokenConverter();
			KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("encrypt.jks"),
					configurableEnvironment.getProperty("jwt.jks.password").toCharArray());
			converter.setKeyPair(keyStoreKeyFactory.getKeyPair(configurableEnvironment.getProperty("jwt.jks.alias")));
			return converter;

		} else if (configurableEnvironment.getProperty("application.security.type").equals("jwt-resserver")) {
			EnhancedJWTAccessTokenConverter converter = new EnhancedJWTAccessTokenConverter();
			String key = null;
			try {
				File file = ResourceUtils.getFile("classpath:publickey.txt");
				key = new String(Files.readAllBytes(file.toPath()));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			converter.setVerifierKey(key);
			return converter;
		} else if (configurableEnvironment.getProperty("application.security.type").equals("jwt-both")) {

			EnhancedJWTAccessTokenConverter converter = new EnhancedJWTAccessTokenConverter();
			KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("encrypt.jks"),
					configurableEnvironment.getProperty("jwt.jks.password").toCharArray());
			converter.setKeyPair(keyStoreKeyFactory.getKeyPair(configurableEnvironment.getProperty("jwt.jks.alias")));

			String key = null;
			try {
				File file = ResourceUtils.getFile("classpath:publickey.txt");
				key = new String(Files.readAllBytes(file.toPath()));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			converter.setVerifierKey(key);
			return converter;
		}

		throw new RuntimeException("AccessTokenConverter couldnot be created.");

	}

}
