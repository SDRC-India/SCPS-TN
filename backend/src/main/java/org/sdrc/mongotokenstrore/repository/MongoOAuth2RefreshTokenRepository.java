package org.sdrc.mongotokenstrore.repository;

import org.sdrc.mongotokenstrore.MongoOAuth2RefreshToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnExpression("'${app.datasource.type}'=='MONGO'")
public interface MongoOAuth2RefreshTokenRepository extends MongoRepository<MongoOAuth2RefreshToken, String>, MongoOAuth2RefreshTokenRepositoryBase {
}