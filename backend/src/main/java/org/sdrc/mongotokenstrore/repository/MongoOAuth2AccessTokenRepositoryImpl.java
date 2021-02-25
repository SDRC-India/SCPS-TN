package org.sdrc.mongotokenstrore.repository;

import java.util.List;

import org.sdrc.mongotokenstrore.MongoOAuth2AccessToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.WriteResult;

@Component
@ConditionalOnExpression("'${app.datasource.type}'=='MONGO'")
public class MongoOAuth2AccessTokenRepositoryImpl implements MongoOAuth2AccessTokenRepositoryBase {

	public static final String ID = "_id";
	private final MongoTemplate mongoTemplate;

	public MongoOAuth2AccessTokenRepositoryImpl(final MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public MongoOAuth2AccessToken findByTokenId(final String tokenId) {
		final Query query = Query.query(Criteria.where(ID).is(tokenId));
		return mongoTemplate.findOne(query, MongoOAuth2AccessToken.class);
	}

	@Override
	public boolean deleteByTokenId(final String tokenId) {
		final Query query = Query.query(Criteria.where(ID).is(tokenId));
		final WriteResult deleteResult = mongoTemplate.remove(query, MongoOAuth2AccessToken.class);
		return deleteResult.wasAcknowledged();
	}

	@Override
	public boolean deleteByRefreshTokenId(String refreshTokenId) {
		final Query query = Query.query(Criteria.where("refreshToken").is(refreshTokenId));
		final WriteResult deleteResult = mongoTemplate.remove(query, MongoOAuth2AccessToken.class);
		return deleteResult.wasAcknowledged();
	}

	@Override
	public MongoOAuth2AccessToken findByAuthenticationId(String key) {
		final Query query = Query.query(Criteria.where("authenticationId").is(key));
		return mongoTemplate.findOne(query, MongoOAuth2AccessToken.class);
	}

	@Override
	public List<MongoOAuth2AccessToken> findByUsernameAndClientId(final String username, final String clientId) {
		final Query query = Query
				.query(Criteria.where("username").is(username).andOperator(Criteria.where("clientId").is(clientId)));
		return mongoTemplate.find(query, MongoOAuth2AccessToken.class);
	}

	@Override
	public List<MongoOAuth2AccessToken> findByClientId(final String clientId) {
		final Query query = Query.query(Criteria.where("clientId").is(clientId));
		return mongoTemplate.find(query, MongoOAuth2AccessToken.class);
	}
}