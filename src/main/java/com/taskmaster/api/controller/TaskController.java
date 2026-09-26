package com.taskmaster.api.controller;

import com.taskmaster.api.dto.CreateTaskRequest;
import com.taskmaster.api.dto.RescheduleTaskRequest;
import com.taskmaster.api.dto.TaskResponse;
import com.taskmaster.application.TaskService;
import com.taskmaster.domain.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> criarTarefa(@RequestBody CreateTaskRequest request) {
        Task task = taskService.criarTarefa(request.title(), request.description(), request.dueDate());
        URI location = URI.create("/api/tasks/" + task.getId());
        return ResponseEntity.created(location).body(TaskResponse.fromDomain(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> buscarPorId(@PathVariable String id) {
        return taskService.buscarPorId(id)
                .map(TaskResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listarTodas() {
        List<TaskResponse> tasks = taskService.listarTodas()
                .stream()
                .map(TaskResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> concluirTarefa(@PathVariable String id) {
        Task task = taskService.concluirTarefa(id);
        return ResponseEntity.ok(TaskResponse.fromDomain(task));
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<TaskResponse> reagendarTarefa(
            @PathVariable String id,
            @RequestBody RescheduleTaskRequest request
    ) {
        Task task = taskService.reagendarTarefa(id, request.newDueDate());
        return ResponseEntity.ok(TaskResponse.fromDomain(task));
    }
}
