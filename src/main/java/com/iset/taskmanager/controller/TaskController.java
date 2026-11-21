package com.iset.taskmanager.controller;

import com.iset.taskmanager.model.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public TaskController() {
        tasks.put(counter.get(), new Task(counter.getAndIncrement(), "Configurer le pipeline Jenkins", true));
        tasks.put(counter.get(), new Task(counter.getAndIncrement(), "Scanner l'image avec Trivy", false));
    }

    @GetMapping
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        long id = counter.getAndIncrement();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        Task task = tasks.get(id);
        if (task == null) return ResponseEntity.notFound().build();
        task.setCompleted(true);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (tasks.remove(id) == null) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
