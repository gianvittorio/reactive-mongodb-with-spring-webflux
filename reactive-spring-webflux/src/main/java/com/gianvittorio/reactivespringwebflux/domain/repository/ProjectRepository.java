package com.gianvittorio.reactivespringwebflux.domain.repository;

import com.gianvittorio.reactivespringwebflux.domain.entity.Project;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends ReactiveMongoRepository<Project, String> {
}
