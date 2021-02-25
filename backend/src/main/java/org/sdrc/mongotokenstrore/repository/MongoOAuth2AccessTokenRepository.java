package org.sdrc.mongotokenstrore.repository;

import org.sdrc.mongotokenstrore.MongoOAuth2AccessToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@ConditionalOnExpression("'${app.datasource.type}'=='MONGO'")
@Repository
public interface MongoOAuth2AccessTokenRepository extends MongoRepository<MongoOAuth2AccessToken, String>, MongoOAuth2AccessTokenRepositoryBase {

}