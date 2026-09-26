package com.taskmaster.application;

import com.taskmaster.domain.Task;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<String, Task> taskRepository = new ConcurrentHashMap<>();

    public Task criarTarefa(String title, String description, LocalDate dueDate) {
        Task task = new Task(title, description, dueDate);
        taskRepository.put(task.getId(), task);
        return task;
    }

    public Optional<Task> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(taskRepository.get(id));
    }

    public List<Task> listarTodas() {
        return Collections.unmodifiableList(new ArrayList<>(taskRepository.values()));
    }

    public Task concluirTarefa(String id) {
        Task task = obterTarefaOuLancarExcecao(id);
        task.concluir();
        return task;
    }

    public Task reagendarTarefa(String id, LocalDate novaData) {
        Task task = obterTarefaOuLancarExcecao(id);
        task.reagendar(novaData);
        return task;
    }

    private Task obterTarefaOuLancarExcecao(String id) {
        return buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Tarefa não encontrada para o ID informado: " + id));
    }
}
