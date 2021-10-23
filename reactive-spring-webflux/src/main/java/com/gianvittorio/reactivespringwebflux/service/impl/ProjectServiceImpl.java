package com.gianvittorio.reactivespringwebflux.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gianvittorio.reactivespringwebflux.domain.entity.Project;
import com.gianvittorio.reactivespringwebflux.domain.entity.Task;
import com.gianvittorio.reactivespringwebflux.domain.repository.ProjectRepository;
import com.gianvittorio.reactivespringwebflux.domain.repository.TaskRepository;
import com.gianvittorio.reactivespringwebflux.service.ProjectService;
import com.gianvittorio.reactivespringwebflux.service.ResultByStartDateAndCost;
import com.gianvittorio.reactivespringwebflux.service.ResultCount;
import com.gianvittorio.reactivespringwebflux.service.ResultProjectTasks;
import com.google.common.primitives.Bytes;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsOperations;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    final ProjectRepository projectRepository;

    final TaskRepository taskRepository;

    final ReactiveMongoTemplate reactiveMongoTemplate;

    final ReactiveGridFsTemplate reactiveGridFsTemplate;

    final ReactiveGridFsOperations reactiveGridFsOperations;

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

    @Override
    @Transactional
    public Mono<Void> saveProjectAndTask(final Mono<Project> projectMono, final Mono<Task> taskMono) {

        return projectMono
                .flatMap(projectRepository::save)
                .then(taskMono)
                .flatMap(taskRepository::save)
                .then();
    }

    @Override
    public Mono<Void> chunkAndSaveProject(final Project project) {

        final String s = serializeToJson(project);
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

        final DBObject metadata = new BasicDBObject();
        metadata.put("projectId", project.getId());

        final DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        final DefaultDataBuffer dataBuffer = factory.wrap(bytes);

        final Mono<DataBuffer> dataBufferMono = Mono.just(dataBuffer);

        return reactiveGridFsTemplate.store(dataBufferMono, project.getId(), metadata)
                .then();
    }

    private String serializeToJson(final Project project) {

        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(project);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Project deserializeFromJson(final String jsonString) {

        Project project = null;

        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            project = objectMapper.readValue(jsonString, Project.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return project;
    }

    @Override
    public Mono<Project> loadProjectFromGrid(final String projectId) {

        final Mono<GridFSFile> file =
                reactiveGridFsTemplate.findOne(
                        Query.query(
                                        Criteria.where("metadata.projectId").is(projectId)
                                )
                                .with(Sort.by(Sort.Direction.DESC, "uploadDate"))
                                .limit(1)
                );

        final Flux<byte[]> bytesSequence =
                file
                        .flatMap(_file -> reactiveGridFsOperations.getResource(_file))
                        .flatMapMany(rgrs -> rgrs.getDownloadStream())
                        .map(dataBuffer -> {
                            final byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read();

                            return bytes;
                        });

        final Mono<Project> totalBytes = bytesSequence
                .collectList()
                .flatMap(bytes -> {
                    final byte[] data = Bytes.concat(bytes.toArray(new byte[bytes.size()][]));
                    final String jsonString = new String(data, StandardCharsets.UTF_8);

                    return Mono.just(deserializeFromJson(jsonString));
                });

        return totalBytes;
    }

    @Override
    public Mono<Void> deleteProjectFromGrid(final String projectId) {
        return reactiveGridFsTemplate.delete(
                        Query.query(Criteria.where("metadata.projectId").is(projectId))
                );
    }
}
