package com.taskmaster.api.dto;

import com.taskmaster.domain.Task;
import com.taskmaster.domain.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDate dueDate
) {
    public static TaskResponse fromDomain(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getDueDate()
        );
    }
}
