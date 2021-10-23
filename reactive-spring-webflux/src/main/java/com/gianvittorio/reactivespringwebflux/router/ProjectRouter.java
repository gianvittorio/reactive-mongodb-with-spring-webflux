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
                        RequestPredicates.GET("/project/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findById
                )
                .andRoute(
                        RequestPredicates.GET("/project/")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findAll
                )
                .andRoute(
                        RequestPredicates.DELETE("/project/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::deleteById
                ).andRoute(
                        RequestPredicates.GET("/project/find/byName")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByName
                ).andRoute(
                        RequestPredicates.GET("/project/find/byNameNot")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByNameNot
                ).andRoute(
                        RequestPredicates.GET("/project/find/byEstimatedCostGreaterThan")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByEstimatedCostGreaterThan
                ).andRoute(
                        RequestPredicates.GET("/project/find/byEstimatedCostBetween")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByEstimatedCostBetween
                ).andRoute(
                        RequestPredicates.GET("/project/find/byNameLike")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByNameLike
                ).andRoute(
                        RequestPredicates.GET("/project/find/byNameRegex")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByNameRegex
                ).andRoute(
                        RequestPredicates.GET("/project/find/byNameQuery")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByNameQuery
                ).andRoute(
                        RequestPredicates.GET("/project/find/byNameAndCostQuery")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByNameAndCostQuery
                ).andRoute(
                        RequestPredicates.GET("/project/find/byEstimatedCostBetweenQuery")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByEstimatedCostBetweenQuery
                ).andRoute(
                        RequestPredicates.GET("/project/find/byNameRegexQuery")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByNameRegexQuery
                ).andRoute(
                        RequestPredicates.GET("/project/find/byProjectNameQueryWithTemplate")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByProjectNameQueryWithTemplate
                ).andRoute(
                        RequestPredicates.GET("/project/find/byEstimatedCostBetweenQueryWithTemplate")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByEstimatedCostBetweenQueryWithTemplate
                ).andRoute(
                        RequestPredicates.GET("/project/find/byNameRegexQueryWithTemplate")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findByNameRegexQueryWithTemplate
                ).andRoute(
                        RequestPredicates.POST("/project/find/upsertCostWithCriteriaTemplate")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::upsertCostWithCriteriaTemplate
                ).andRoute(
                        RequestPredicates.DELETE("/project/find/deleteWithCriteriaTemplate")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::deleteWithCriteriaTemplate
                ).andRoute(
                        RequestPredicates.GET("/project/find/findCostsGroupByStartDateForProjectsCostGreaterThan")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findCostsGroupByStartDateForProjectsCostGreaterThan
                )
                .andRoute(
                        RequestPredicates.GET("/project/find/findNoOfProjectsCostGreaterThan")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findNoOfProjectsCostGreaterThan
                ).andRoute(
                        RequestPredicates.GET("/project/find/findAllProjectTasks")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        handler::findAllProjectTasks
                );
    }
}
