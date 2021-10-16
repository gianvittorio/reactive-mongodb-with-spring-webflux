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
}
