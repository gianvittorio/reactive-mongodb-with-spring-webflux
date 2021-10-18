package com.gianvittorio.reactivespringwebflux.domain.repository;

import com.gianvittorio.reactivespringwebflux.domain.entity.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProjectRepository extends ReactiveMongoRepository<Project, String> {

    Flux<Project> findByName(final String name);

    Flux<Project> findByNameNot(final String name);

    Flux<Project> findByEstimatedCostGreaterThan(final Long cost);

    Flux<Project> findByEstimatedCostBetween(final Long from, final Long to);

    Flux<Project> findByNameLike(final String name);

    Flux<Project> findByNameRegex(final String name);

    @Query("{'name': ?0}")
    Flux<Project> findByNameQuery(final String name);

    @Query("{ $and : [ {'name' : ?0}, {'cost' : ?1} ] }")
    Flux<Project> findByNameAndCostQuery(final String name, final Long cost);

    @Query("{ 'cost': { $lt : ?1, $gt : ?0 } }")
    Flux<Project> findByEstimatedCostBetweenQuery(final Long from, final Long to, final Sort sort);

    @Query(value = "{name : { $regex: ?0} }", fields = "{ 'name' : 1, 'cost' : 1 }")
    Flux<Project> findByNameRegexQuery(final String name);
}
