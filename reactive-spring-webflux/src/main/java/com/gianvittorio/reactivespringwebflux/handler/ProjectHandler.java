package com.gianvittorio.reactivespringwebflux.handler;

import com.gianvittorio.reactivespringwebflux.domain.entity.Project;
import com.gianvittorio.reactivespringwebflux.domain.entity.Task;
import com.gianvittorio.reactivespringwebflux.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProjectHandler {

    final ProjectService projectService;

    public Mono<ServerResponse> createProject(final ServerRequest serverRequest) {

        return Mono.just(serverRequest)
                .flatMap(request -> request.bodyToMono(Project.class))
                .flatMap(projectService::createProject)
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data));
    }

    public Mono<ServerResponse> createTask(final ServerRequest serverRequest) {

        return Mono.just(serverRequest)
                .flatMap(request -> request.bodyToMono(Task.class))
                .flatMap(projectService::createTask)
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data));
    }

    public Mono<ServerResponse> findById(final ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");

        return projectService.findById(id)
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findAll(final ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(projectService.findAll(), Project.class);
    }

    public Mono<ServerResponse> deleteById(final ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");

        return projectService.deleteById(id)
                .then(ServerResponse.noContent().build())
                .log();
    }
}
