package com.gianvittorio.reactivespringwebflux.router;

import com.gianvittorio.reactivespringwebflux.handler.ProjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProjectRouter {

    @Bean
    public RouterFunction<ServerResponse> routeProjects(final ProjectHandler handler) {

        return RouterFunctions
                .route(
                        RequestPredicates.POST("/project/create")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::createProject
                )
                .andRoute(
                        RequestPredicates.POST("/project/createTask")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::createTask
                )
                .andRoute(
                    RequestPredicates.GET("/project/{id}"),
//                            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findById
                )
                .andRoute(
                        RequestPredicates.GET("/project/"),
                                //.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findAll
                )
                .andRoute(
                    RequestPredicates.DELETE("/project/{id}"),
                            //.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::deleteById
                );
    }
}
