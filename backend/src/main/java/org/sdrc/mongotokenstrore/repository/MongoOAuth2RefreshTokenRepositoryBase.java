package org.sdrc.mongotokenstrore.repository;

import org.sdrc.mongotokenstrore.MongoOAuth2RefreshToken;

public interface MongoOAuth2RefreshTokenRepositoryBase  {

	 MongoOAuth2RefreshToken findByTokenId(String tokenId);

	    boolean deleteByTokenId(String tokenId);
}
