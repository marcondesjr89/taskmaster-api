package com.taskmaster.application;

import com.taskmaster.domain.Task;
import com.taskmaster.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskService - Testes Unitários da Camada de Aplicação")
class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Nested
    @DisplayName("Criação de Tarefas")
    class CriacaoTarefas {

        @Test
        @DisplayName("Deve criar e armazenar uma tarefa com sucesso")
        void deveCriarTarefaComSucesso() {
            LocalDate amanha = LocalDate.now().plusDays(1);
            Task task = taskService.criarTarefa("Estudar Clean Code", "Leitura do capítulo 3", amanha);

            assertNotNull(task);
            assertNotNull(task.getId());
            assertEquals("Estudar Clean Code", task.getTitle());
            assertEquals("Leitura do capítulo 3", task.getDescription());
            assertEquals(amanha, task.getDueDate());
            assertEquals(TaskStatus.PENDING, task.getStatus());
            assertNotNull(task.getCreatedAt());
        }

        @Test
        @DisplayName("Deve falhar ao criar tarefa com dados inválidos (repassando invariante do domínio)")
        void deveFalharAoCriarTarefaInvalida() {
            LocalDate ontem = LocalDate.now().minusDays(1);
            assertThrows(IllegalArgumentException.class, () ->
                    taskService.criarTarefa("Oi", "Descrição válida", LocalDate.now().plusDays(1))
            );
            assertThrows(IllegalArgumentException.class, () ->
                    taskService.criarTarefa("Título válido", "Descrição", ontem)
            );
        }
    }

    @Nested
    @DisplayName("Busca de Tarefas")
    class BuscaTarefas {

        @Test
        @DisplayName("Deve retornar a tarefa quando buscada por um ID existente")
        void deveBuscarTarefaPorIdExistente() {
            Task criada = taskService.criarTarefa("Fazer compras", "Itens da semana", LocalDate.now().plusDays(2));

            Optional<Task> encontrada = taskService.buscarPorId(criada.getId());

            assertTrue(encontrada.isPresent());
            assertEquals(criada.getId(), encontrada.get().getId());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio ao buscar por ID inexistente ou nulo")
        void deveRetornarVazioParaIdInexistenteOuNulo() {
            Optional<Task> resultadoInexistente = taskService.buscarPorId("id-nao-existente");
            Optional<Task> resultadoNulo = taskService.buscarPorId(null);
            Optional<Task> resultadoVazio = taskService.buscarPorId("   ");

            assertTrue(resultadoInexistente.isEmpty());
            assertTrue(resultadoNulo.isEmpty());
            assertTrue(resultadoVazio.isEmpty());
        }

        @Test
        @DisplayName("Deve listar todas as tarefas cadastradas")
        void deveListarTodasAsTarefas() {
            taskService.criarTarefa("Tarefa 1", "Desc 1", LocalDate.now().plusDays(1));
            taskService.criarTarefa("Tarefa 2", "Desc 2", LocalDate.now().plusDays(2));

            List<Task> lista = taskService.listarTodas();

            assertEquals(2, lista.size());
        }
    }

    @Nested
    @DisplayName("Conclusão de Tarefas")
    class ConclusaoTarefas {

        @Test
        @DisplayName("Deve concluir uma tarefa existente com sucesso")
        void deveConcluirTarefaComSucesso() {
            Task task = taskService.criarTarefa("Finalizar módulo", "Etapa 2", LocalDate.now().plusDays(3));

            Task concluida = taskService.concluirTarefa(task.getId());

            assertEquals(TaskStatus.COMPLETED, concluida.getStatus());
        }

        @Test
        @DisplayName("Deve lançar NoSuchElementException ao tentar concluir tarefa com ID inexistente")
        void deveLancarExcecaoAoConcluirTarefaInexistente() {
            assertThrows(NoSuchElementException.class, () ->
                    taskService.concluirTarefa("id-fantasma")
            );
        }
    }

    @Nested
    @DisplayName("Reagendamento de Tarefas")
    class ReagendamentoTarefas {

        @Test
        @DisplayName("Deve reagendar uma tarefa com sucesso para uma data válida")
        void deveReagendarTarefaComSucesso() {
            Task task = taskService.criarTarefa("Reunião 1-on-1", "Alinhamento", LocalDate.now().plusDays(1));
            LocalDate novaData = LocalDate.now().plusDays(7);

            Task reagendada = taskService.reagendarTarefa(task.getId(), novaData);

            assertEquals(novaData, reagendada.getDueDate());
        }

        @Test
        @DisplayName("Deve lançar NoSuchElementException ao tentar reagendar tarefa com ID inexistente")
        void deveLancarExcecaoAoReagendarTarefaInexistente() {
            LocalDate novaData = LocalDate.now().plusDays(5);
            assertThrows(NoSuchElementException.class, () ->
                    taskService.reagendarTarefa("id-fantasma", novaData)
            );
        }
    }
}
