package org.sdrc.mongotokenstrore.repository;

import org.sdrc.mongotokenstrore.MongoOAuth2RefreshToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.WriteResult;

@ConditionalOnExpression("'${app.datasource.type}'=='MONGO'")
@Component
public class MongoOAuth2RefreshTokenRepositoryImpl implements MongoOAuth2RefreshTokenRepositoryBase {

	public static final String ID = "_id";
	private MongoTemplate mongoTemplate;

	public MongoOAuth2RefreshTokenRepositoryImpl(final MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public MongoOAuth2RefreshToken findByTokenId(final String tokenId) {
		final Query query = Query.query(Criteria.where(ID).is(tokenId));
		return mongoTemplate.findOne(query, MongoOAuth2RefreshToken.class);
	}

	@Override
	public boolean deleteByTokenId(final String tokenId) {
		final Query query = Query.query(Criteria.where(ID).is(tokenId));
		final WriteResult deleteResult = mongoTemplate.remove(query, MongoOAuth2RefreshToken.class);
		return deleteResult.wasAcknowledged();
	}
}