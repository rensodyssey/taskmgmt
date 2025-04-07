package com.example.taskmgmt.repository;

import com.example.taskmgmt.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
