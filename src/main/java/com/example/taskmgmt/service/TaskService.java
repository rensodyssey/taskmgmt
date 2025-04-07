package com.example.taskmgmt.service;

import com.example.taskmgmt.config.OpenTelemetryConfig;
import com.example.taskmgmt.entity.Task;
import com.example.taskmgmt.entity.User;
import com.example.taskmgmt.repository.TaskRepository;
import com.example.taskmgmt.repository.UserRepository;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static final Tracer tracer = OpenTelemetryConfig.tracer();

    public List<Task> getAllTasks() {
        Span span = tracer.spanBuilder("TaskService.getAllTasks").startSpan();
        try {
            logger.info("Fetching all tasks");
            return taskRepo.findAll();
        } finally {
            span.end();
        }
    }

    public Task createTask(Task task) {
        Span span = tracer.spanBuilder("TaskService.createTask").startSpan();
        try {
            task.setCreatedAt(LocalDateTime.now());
            logger.info("Creating task: {}", task.getTitle());
            return taskRepo.save(task);
        } finally {
            span.end();
        }
    }

    public Optional<Task> updateTask(Long id, Task updatedTask) {
        Span span = tracer.spanBuilder("TaskService.updateTask").startSpan();
        try {
            return taskRepo.findById(id).map(task -> {
                task.setTitle(updatedTask.getTitle());
                task.setDescription(updatedTask.getDescription());
                task.setStatus(updatedTask.getStatus());
                logger.info("Updating task {}", id);
                return taskRepo.save(task);
            });
        } finally {
            span.end();
        }
    }

    public boolean deleteTask(Long id) {
        Span span = tracer.spanBuilder("TaskService.deleteTask").startSpan();
        try {
            logger.info("Deleting task {}", id);
            taskRepo.deleteById(id);
            return true;
        } finally {
            span.end();
        }
    }

    public Optional<Task> assignTask(Long taskId, Long userId) {
        Span span = tracer.spanBuilder("TaskService.assignTask").startSpan();
        try {
            Optional<Task> taskOpt = taskRepo.findById(taskId);
            Optional<User> userOpt = userRepo.findById(userId);

            if (taskOpt.isPresent() && userOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setAssignee(userOpt.get());
                logger.info("Assigning task {} to user {}", taskId, userId);
                return Optional.of(taskRepo.save(task));
            }
            return Optional.empty();
        } finally {
            span.end();
        }
    }


}
