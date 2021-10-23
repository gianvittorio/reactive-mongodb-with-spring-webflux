package com.gianvittorio.reactivespringwebflux.service.impl;

import com.gianvittorio.reactivespringwebflux.domain.entity.Project;
import com.gianvittorio.reactivespringwebflux.domain.entity.Task;
import com.gianvittorio.reactivespringwebflux.domain.repository.ProjectRepository;
import com.gianvittorio.reactivespringwebflux.domain.repository.TaskRepository;
import com.gianvittorio.reactivespringwebflux.service.ProjectService;
import com.gianvittorio.reactivespringwebflux.service.ResultByStartDateAndCost;
import com.gianvittorio.reactivespringwebflux.service.ResultCount;
import com.gianvittorio.reactivespringwebflux.service.ResultProjectTasks;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    final ProjectRepository projectRepository;

    final TaskRepository taskRepository;

    final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Project> createProject(final Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Mono<Task> createTask(final Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Mono<Project> findById(final String id) {
        return projectRepository.findById(id);
    }

    @Override
    public Flux<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Mono<Void> deleteById(final String id) {
        return projectRepository.deleteById(id);
    }

    @Override
    public Flux<Project> findByName(final String name) {
        return projectRepository.findByName(name);
    }

    @Override
    public Flux<Project> findByNameNot(final String name) {
        return projectRepository.findByNameNot(name);
    }

    @Override
    public Flux<Project> findByEstimatedCostGreaterThan(final Long cost) {
        return projectRepository.findByEstimatedCostGreaterThan(cost);
    }

    @Override
    public Flux<Project> findByEstimatedCostBetween(final Long from, final Long to) {
        return projectRepository.findByEstimatedCostBetween(from, to);
    }

    @Override
    public Flux<Project> findByNameLike(final String name) {
        return projectRepository.findByNameLike(name);
    }

    @Override
    public Flux<Project> findByNameRegex(final String name) {
        return projectRepository.findByNameRegex(name);
    }

    @Override
    public Flux<Project> findByNameQuery(final String name) {
        return projectRepository.findByNameQuery(name);
    }

    @Override
    public Flux<Project> findByNameAndCostQuery(final String name, final Long cost) {
        return projectRepository.findByNameAndCostQuery(name, cost);
    }

    @Override
    public Flux<Project> findByEstimatedCostBetweenQuery(final Long from, final Long to) {
        return projectRepository.findByEstimatedCostBetweenQuery(from, to, Sort.by(Sort.Direction.DESC, "cost"));
    }

    @Override
    public Flux<Project> findByNameRegexQuery(final String name) {
        return projectRepository.findByNameRegexQuery(name);
    }

    @Override
    public Flux<Project> findByProjectNameQueryWithTemplate(final String name) {

        final var query = new Query();
        query.addCriteria(Criteria.where("name").is(name));

        return reactiveMongoTemplate.find(query, Project.class);
    }

    @Override
    public Flux<Project> findByEstimatedCostBetweenQueryWithTemplate(final Long from, final Long to) {

        return reactiveMongoTemplate.find(
                Query.query(
                                Criteria
                                        .where("from").gt(from)
                                        .and("to").lt(to)
                        )
                        .with(Sort.by(Sort.Direction.ASC, "cost")),
                Project.class
        );
    }

    @Override
    public Flux<Project> findByNameRegexQueryWithTemplate(final String name) {
        return reactiveMongoTemplate.find(
                Query.query(Criteria.where("name").regex(name)),
                Project.class
        );
    }

    @Override
    public Mono<Void> upsertCostWithCriteriaTemplate(final String id, final Long cost) {

        return reactiveMongoTemplate.upsert(
                        Query.query(Criteria.where("id").is(id)),
                        Update.update("cost", cost),
                        Project.class
                )
                .then();
    }

    @Override
    public Mono<Void> deleteWithCriteriaTemplate(final String id) {

        return reactiveMongoTemplate.remove(
                        Query.query(Criteria.where("id")
                                .is(id)
                        )
                )
                .then();
    }

    @Override
    public Mono<Long> findNoOfProjectsCostGreaterThan(final Long cost) {

        final MatchOperation matchStage = Aggregation.match(Criteria.where("cost").gt(cost));
        final CountOperation countStage = Aggregation.count().as("costlyProjects");

        final Aggregation aggregation = Aggregation.newAggregation(matchStage, countStage);
        final Flux<ResultCount> output = reactiveMongoTemplate.aggregate(aggregation, "project", ResultCount.class);
        final Flux<Long> resultc = output.map(result -> result.getCostlyProjects()).switchIfEmpty(Flux.just(0l));

        return resultc.take(1).single();
    }

    @Override
    public Flux<ResultByStartDateAndCost> findCostsGroupByStartDateForProjectsCostGreaterThan(final Long cost) {

        final MatchOperation filterCost = Aggregation.match(Criteria.where("cost").gt(cost));
        final GroupOperation groupByStartDateAndSumCost =
                Aggregation.group("startDate")
                        .sum("cost")
                        .as("total");

        final SortOperation sortByTotal = Aggregation.sort(Sort.Direction.DESC, "total");

        final Aggregation aggregation = Aggregation.newAggregation(filterCost, groupByStartDateAndSumCost, sortByTotal);

        return reactiveMongoTemplate.aggregate(aggregation, "project", ResultByStartDateAndCost.class);
    }

    @Override
    public Flux<ResultProjectTasks> findAllProjectTasks() {

        final LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("task")
                .localField("_id")
                .foreignField("pid")
                .as("ProjectTasks");
        final UnwindOperation unwindOperation = Aggregation.unwind("ProjectTasks");
        final ProjectionOperation projectionOperation = Aggregation
                .project()
                .andExpression("_id").as("_id")
                .andExpression("name").as("name")
                .andExpression("ProjectTasks.name").as("taskName")
                .andExpression("ProjectTasks.ownername").as("taskOwnerName");
        final Aggregation aggregation = Aggregation.newAggregation(lookupOperation, unwindOperation, projectionOperation);

        return reactiveMongoTemplate.aggregate(aggregation, "project", ResultProjectTasks.class);
    }
}
