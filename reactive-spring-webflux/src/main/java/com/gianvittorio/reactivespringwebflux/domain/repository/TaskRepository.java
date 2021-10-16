package com.gianvittorio.reactivespringwebflux.domain.repository;

import com.gianvittorio.reactivespringwebflux.domain.entity.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
}
