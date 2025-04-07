package com.example.taskmgmt.controller;

import com.example.taskmgmt.entity.Task;
import com.example.taskmgmt.service.TaskService;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.taskmgmt.config.OpenTelemetryConfig.tracer;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private static final Tracer otelTracer = tracer();

    @GetMapping
    public List<Task> getAll() {
        Span span = otelTracer.spanBuilder("TaskController.getAll").startSpan();
        try {
            logger.info("GET /api/tasks");
            return taskService.getAllTasks();
        } finally {
            span.end();
        }
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        Span span = otelTracer.spanBuilder("TaskController.create").startSpan();
        try {
            logger.info("POST /api/tasks - {}", task.getTitle());
            Task created = taskService.createTask(task);
            return ResponseEntity.ok(created);
        } finally {
            span.end();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody Task task) {
        Span span = otelTracer.spanBuilder("TaskController.update").startSpan();
        try {
            return taskService.updateTask(id, task)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Span span = otelTracer.spanBuilder("TaskController.delete").startSpan();
        try {
            logger.info("DELETE /api/tasks/{}", id);
            boolean deleted = taskService.deleteTask(id);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } finally {
            span.end();
        }
    }

    @PostMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assign(@PathVariable Long taskId, @PathVariable Long userId) {
        Span span = otelTracer.spanBuilder("TaskController.assign").startSpan();
        try {
            logger.info("POST /api/tasks/{}/assign/{}", taskId, userId);
            return taskService.assignTask(taskId, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } finally {
            span.end();
        }
    }
}
