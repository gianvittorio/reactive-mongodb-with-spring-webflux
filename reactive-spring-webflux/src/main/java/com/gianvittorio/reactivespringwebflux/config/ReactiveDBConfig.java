package com.gianvittorio.reactivespringwebflux.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = {"com/gianvittorio/reactivespringwebflux/domain/repository"})
public class ReactiveDBConfig extends AbstractReactiveMongoConfiguration {

    @Value("${udemy.mongodb.replicaset.name}")
    private String replicasetName;

    @Value("${udemy.mongodb.replicaset.username}")
    private String replicasetUsername;

    @Value("${udemy.mongodb.replicaset.password}")
    private String replicasetPassword;

    @Value("${udemy.mongodb.replicaset.primary}")
    private String replicasetPrimary;

    @Value("${udemy.mongodb.replicaset.port}")
    private String replicasetPort;

    @Value("${udemy.mongodb.replicaset.database}")
    private String database;

    @Value("${udemy.mongodb.replicaset.authentication-database}")
    private String replicasetAuthenticationDb;

    @Bean
    @Override
    public MongoClient reactiveMongoClient() {

        final String connectionString = String.format("mongodb://%s:%s@%s:%s/%s?replicaSet=%s&authSource=%s",
                replicasetUsername,
                replicasetPassword,
                replicasetPrimary,
                replicasetPort,
                database,
                replicasetName,
                replicasetAuthenticationDb
        );

        return MongoClients.create(connectionString);
    }

    @Bean
    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(final MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, this.getDatabaseName());
    }

    @Bean
    public ReactiveMongoTransactionManager reactiveMongoTransactionManager(final ReactiveMongoDatabaseFactory factory) {
        return new ReactiveMongoTransactionManager(factory);
    }

    @Bean
    public ReactiveGridFsTemplate reactiveGridFsTemplate(final MappingMongoConverter mongoConverter) throws Exception {
        return new ReactiveGridFsTemplate(reactiveMongoDbFactory(), mongoConverter);
    }
}
