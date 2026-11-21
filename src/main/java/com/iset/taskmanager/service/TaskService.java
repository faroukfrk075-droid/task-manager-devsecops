package com.iset.taskmanager.service;

import com.iset.taskmanager.model.Task;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final Map<Long, Task> taskStore = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public TaskService() {
        createTask("Configurer le pipeline Jenkins");
        createTask("Scanner l'image Docker avec Trivy");
        createTask("Déployer sur Kubernetes Minikube");
    }

    public Collection<Task> findAll() {
        return taskStore.values();
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(taskStore.get(id));
    }

    public Task createTask(String title) {
        long id = idGenerator.getAndIncrement();
        Task task = new Task(id, title, false);
        taskStore.put(id, task);
        return task;
    }

    public Optional<Task> markAsCompleted(Long id) {
        Task task = taskStore.get(id);
        if (task == null) {
            return Optional.empty();
        }
        task.setCompleted(true);
        return Optional.of(task);
    }

    public boolean deleteById(Long id) {
        return taskStore.remove(id) != null;
    }
}
