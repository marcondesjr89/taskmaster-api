package com.taskmaster.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmaster.api.dto.CreateTaskRequest;
import com.taskmaster.api.dto.RescheduleTaskRequest;
import com.taskmaster.application.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TaskController - Testes de Integração da Camada de API REST")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/tasks - Deve criar tarefa com sucesso e retornar 201 Created")
    void deveCriarTarefaComSucesso() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Estudar Spring Boot",
                "Controllers e DTOs",
                LocalDate.now().plusDays(2)
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Estudar Spring Boot"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/tasks - Deve retornar 400 Bad Request ao passar dados inválidos")
    void deveRetornar400AoCriarComDadosInvalidos() throws Exception {
        CreateTaskRequest requestInvalido = new CreateTaskRequest(
                "Oi",
                "Descrição",
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("O título da tarefa deve ter pelo menos 3 caracteres."));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Deve retornar 404 Not Found para tarefa inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/tasks/id-fantasma-123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/complete - Deve concluir tarefa com sucesso")
    void deveConcluirTarefaComSucesso() throws Exception {
        var task = taskService.criarTarefa("Tarefa para concluir", "Desc", LocalDate.now().plusDays(1));

        mockMvc.perform(patch("/api/tasks/" + task.getId() + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/reschedule - Deve reagendar tarefa com sucesso")
    void deveReagendarTarefaComSucesso() throws Exception {
        var task = taskService.criarTarefa("Tarefa para reagendar", "Desc", LocalDate.now().plusDays(1));
        LocalDate novaData = LocalDate.now().plusDays(10);
        RescheduleTaskRequest request = new RescheduleTaskRequest(novaData);

        mockMvc.perform(patch("/api/tasks/" + task.getId() + "/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dueDate").value(novaData.toString()));
    }
}
