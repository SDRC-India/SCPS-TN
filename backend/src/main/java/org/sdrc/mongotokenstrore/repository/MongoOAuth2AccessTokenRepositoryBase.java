package org.sdrc.mongotokenstrore.repository;

import java.util.List;

import org.sdrc.mongotokenstrore.MongoOAuth2AccessToken;

public interface MongoOAuth2AccessTokenRepositoryBase {
	
	MongoOAuth2AccessToken findByTokenId(String tokenId);

	boolean deleteByTokenId(String tokenId);

	boolean deleteByRefreshTokenId(String refreshTokenId);

	MongoOAuth2AccessToken findByAuthenticationId(String key);

	List<MongoOAuth2AccessToken> findByUsernameAndClientId(String username, String clientId);

	List<MongoOAuth2AccessToken> findByClientId(String clientId);
}
