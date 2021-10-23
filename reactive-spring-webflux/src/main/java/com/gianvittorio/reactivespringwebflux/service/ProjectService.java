package com.gianvittorio.reactivespringwebflux.service;


import com.gianvittorio.reactivespringwebflux.domain.entity.Project;
import com.gianvittorio.reactivespringwebflux.domain.entity.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectService {

    Mono<Project> createProject(final Project project);

    Mono<Task> createTask(final Task task);

    Mono<Project> findById(final String id);

    Flux<Project> findAll();

    Mono<Void> deleteById(final String id);

    Flux<Project> findByName(final String name);

    Flux<Project> findByNameNot(final String name);

    Flux<Project> findByEstimatedCostGreaterThan(final Long cost);

    Flux<Project> findByEstimatedCostBetween(final Long from, final Long to);

    Flux<Project> findByNameLike(final String name);

    Flux<Project> findByNameRegex(final String name);

    Flux<Project> findByNameQuery(final String name);

    Flux<Project> findByNameAndCostQuery(final String name, final Long cost);

    Flux<Project> findByEstimatedCostBetweenQuery(final Long from, final Long to);

    Flux<Project> findByNameRegexQuery(final String name);

    Flux<Project> findByProjectNameQueryWithTemplate(final String name);

    Flux<Project> findByEstimatedCostBetweenQueryWithTemplate(final Long from, final Long to);

    Flux<Project> findByNameRegexQueryWithTemplate(final String name);

    Mono<Void> upsertCostWithCriteriaTemplate(final String id, final Long cost);

    Mono<Void> deleteWithCriteriaTemplate(final String id);
}
