package com.gianvittorio.reactivespringwebflux.service.impl;

import com.gianvittorio.reactivespringwebflux.domain.entity.Project;
import com.gianvittorio.reactivespringwebflux.domain.entity.Task;
import com.gianvittorio.reactivespringwebflux.domain.repository.ProjectRepository;
import com.gianvittorio.reactivespringwebflux.domain.repository.TaskRepository;
import com.gianvittorio.reactivespringwebflux.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    final ProjectRepository projectRepository;

    final TaskRepository taskRepository;

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
}
