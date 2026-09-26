package com.taskmaster.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private final String id;
    private String title;
    private String description;
    private TaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDate dueDate;

    public Task(String title, String description, LocalDate dueDate) {
        validateTitle(title);
        validateDueDate(dueDate);

        this.id = UUID.randomUUID().toString();
        this.title = title.trim();
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.dueDate = dueDate;
    }

    public void concluir() {
        if (status == TaskStatus.COMPLETED) {
            throw new IllegalStateException("A tarefa já está concluída.");
        }
        if (status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Uma tarefa cancelada não pode ser concluída.");
        }
        this.status = TaskStatus.COMPLETED;
    }

    public void reagendar(LocalDate novaData) {
        validateDueDate(novaData);
        if (status == TaskStatus.COMPLETED) {
            throw new IllegalStateException("Uma tarefa concluída não pode ser reagendada.");
        }
        if (status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Uma tarefa cancelada não pode ser reagendada.");
        }
        this.dueDate = novaData;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank() || title.trim().length() < 3) {
            throw new IllegalArgumentException("O título da tarefa deve ter pelo menos 3 caracteres.");
        }
    }

    private void validateDueDate(LocalDate date) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de entrega não pode ser anterior à data atual.");
        }
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDate getDueDate() { return dueDate; }
}