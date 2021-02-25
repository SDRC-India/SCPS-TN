package org.sdrc.mongoclientdetails.repository;

import org.sdrc.mongoclientdetails.MongoClientDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.repository.MongoRepository;

@ConditionalOnExpression("'${app.datasource.type}'=='MONGO'")
public interface MongoClientDetailsRepository extends MongoRepository<MongoClientDetails, String>, MongoClientDetailsRepositoryBase {
}