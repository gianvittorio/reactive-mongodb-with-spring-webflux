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

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ProjectHandler {

    final ProjectService projectService;

    public Mono<ServerResponse> createProject(final ServerRequest serverRequest) {

        return Mono.just(serverRequest)
                .flatMap(request -> request.bodyToMono(Project.class))
                .flatMap(projectService::createProject)
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data));
//                .onErrorResume(error -> {
//                    if (error instanceof OptimisticLockingFailureException) {
//                        return ServerResponse.badRequest().build();
//                    }
//
//                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .build();
//                });
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

    public Mono<ServerResponse> findByName(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByName)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByNameNot(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByNameNot)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByEstimatedCostGreaterThan(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("cost"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .map(Long::parseLong)
                .flatMapMany(projectService::findByEstimatedCostGreaterThan)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByEstimatedCostBetween(final ServerRequest serverRequest) {
        final Mono<Long> fromMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("from"))
                .flatMap(fromOptional -> fromOptional.map(Mono::just).orElse(Mono.empty()))
                .map(Long::parseLong);

        final Mono<Long> toMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("to"))
                .flatMap(fromOptional -> fromOptional.map(Mono::just).orElse(Mono.empty()))
                .map(Long::parseLong);

        return fromMono.zipWith(toMono, projectService::findByEstimatedCostBetween)
                .flatMapMany(Function.identity())
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByNameLike(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByNameLike)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByNameRegex(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByNameRegex)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByNameQuery(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByNameQuery)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByNameAndCostQuery(final ServerRequest serverRequest) {

        final Mono<String> nameMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(name -> name.map(Mono::just).orElse(Mono.empty()));

        final Mono<Long> costMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("cost"))
                .flatMap(cost -> cost.map(Mono::just).orElse(Mono.empty()))
                .map(Long::parseLong);

        return nameMono.zipWith(costMono, projectService::findByNameAndCostQuery)
                .flatMapMany(Function.identity())
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByEstimatedCostBetweenQuery(final ServerRequest serverRequest) {

        final Mono<Long> fromMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("from"))
                .flatMap(cost -> cost.map(Mono::just).orElse(Mono.empty()))
                .map(Long::parseLong);


        final Mono<Long> toMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("to"))
                .flatMap(cost -> cost.map(Mono::just).orElse(Mono.empty()))
                .map(Long::parseLong);

        return fromMono.zipWith(toMono, projectService::findByEstimatedCostBetweenQuery)
                .flatMapMany(Function.identity())
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByNameRegexQuery(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByNameRegexQuery)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByProjectNameQueryWithTemplate(final ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByProjectNameQueryWithTemplate)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByEstimatedCostBetweenQueryWithTemplate(final ServerRequest serverRequest) {

        final Function<? super Mono<Optional<String>>, ? extends Mono<Long>> paramMapper =
                (paramOptional) ->
                        paramOptional
                                .flatMap(
                                        optional -> optional
                                                .map(Mono::just)
                                                .orElse(Mono.empty())
                                )
                                .map(Long::parseLong);

        final Mono<Long> fromMono = Mono.just(serverRequest.queryParam("from"))
                .transform(paramMapper);

        final Mono<Long> toMono = Mono.just(serverRequest.queryParam("to"))
                .transform(paramMapper);

        return fromMono.zipWith(toMono, projectService::findByEstimatedCostBetweenQueryWithTemplate)
                .flatMapMany(Function.identity())
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findByNameRegexQueryWithTemplate(final ServerRequest serverRequest) {

        return Mono.just(serverRequest)
                .map(request -> request.queryParam("name"))
                .flatMap(nameOptional -> nameOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMapMany(projectService::findByNameRegexQueryWithTemplate)
                .collectList()
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> upsertCostWithCriteriaTemplate(final ServerRequest serverRequest) {
        final Mono<String> idMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("id"))
                .flatMap(cost -> cost.map(Mono::just).orElse(Mono.empty()));


        final Mono<Long> costMono = Mono.just(serverRequest)
                .map(request -> request.queryParam("cost"))
                .flatMap(cost -> cost.map(Mono::just).orElse(Mono.empty()))
                .map(Long::parseLong);

        return idMono.zipWith(costMono, projectService::upsertCostWithCriteriaTemplate)
                .flatMap(Function.identity())
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> deleteWithCriteriaTemplate(final ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParam("id"))
                .flatMap(idOptional -> idOptional.map(Mono::just).orElse(Mono.empty()))
                .flatMap(projectService::deleteWithCriteriaTemplate)
                .then(ServerResponse.noContent().build());
    }
}
