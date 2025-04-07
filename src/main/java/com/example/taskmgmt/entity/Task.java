package com.example.taskmgmt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private String status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "asignee_id")
    private User assignee;
}
