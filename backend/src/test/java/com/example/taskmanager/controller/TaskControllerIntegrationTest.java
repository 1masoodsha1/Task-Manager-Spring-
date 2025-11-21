package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    private Task createSampleTask(
            String title,
            TaskStatus status,
            LocalDate dueDate,
            int priority,
            int estimatedMinutes
    ) {
        Task t = new Task();
        t.setTitle(title);
        t.setDescription("Test description");
        t.setStatus(status);
        t.setDueDate(dueDate);
        t.setPriority(priority);
        t.setEstimatedMinutes(estimatedMinutes);
        return taskRepository.save(t);
    }

    // ---- Basic CRUD endpoint tests ----

    @Test
    void createTask_andGetItBack() throws Exception {
        String json = """
            {
              "title": "New Task",
              "description": "Create task via API",
              "status": "TODO",
              "dueDate": "2030-01-01",
              "priority": 4,
              "estimatedMinutes": 45
            }
            """;

        // Create
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.priority").value(4))
                .andExpect(jsonPath("$.estimatedMinutes").value(45));

        // List
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("New Task"));
    }

    @Test
    void updateTask_changesPriorityAndEstimatedMinutes() throws Exception {
        Task existing = createSampleTask(
                "Original",
                TaskStatus.TODO,
                LocalDate.of(2030, 1, 1),
                2,
                30
        );

        String json = """
            {
              "id": %d,
              "title": "Updated Title",
              "description": "Updated description",
              "status": "TODO",
              "dueDate": "2030-01-01",
              "priority": 5,
              "estimatedMinutes": 60
            }
            """.formatted(existing.getId());

        mockMvc.perform(put("/api/tasks/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.priority").value(5))
                .andExpect(jsonPath("$.estimatedMinutes").value(60));
    }

    @Test
    void deleteTask_removesItFromList() throws Exception {
        Task existing = createSampleTask(
                "To delete",
                TaskStatus.TODO,
                LocalDate.now().plusDays(5),
                3,
                45
        );

        mockMvc.perform(delete("/api/tasks/{id}", existing.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ---- Planner endpoint tests ----

    @Test
    void planner_returnsOverdueHighPriorityBeforeFutureLowPriority() throws Exception {
        LocalDate today = LocalDate.now();

        // Overdue high-priority TODO task
        createSampleTask(
                "Overdue high priority",
                TaskStatus.TODO,
                today.minusDays(1),
                5,
                60
        );

        // Future low-priority TODO task
        createSampleTask(
                "Future low priority",
                TaskStatus.TODO,
                today.plusDays(7),
                1,
                60
        );

        // Request with enough time for only one 60-minute task
        String requestJson = """
            { "availableMinutes": 60 }
            """;

        mockMvc.perform(post("/api/tasks/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutes").value(60))
                .andExpect(jsonPath("$.remainingMinutes").value(0))
                .andExpect(jsonPath("$.tasks", hasSize(1)))
                .andExpect(jsonPath("$.tasks[0].title").value("Overdue high priority"));
    }

    @Test
    void planner_picksSmallerTasksWhenBigTaskDoesNotFit() throws Exception {
        LocalDate today = LocalDate.now();

        // Big task: won't fit in 50 minutes
        createSampleTask(
                "Big task",
                TaskStatus.TODO,
                today,
                5,
                90
        );

        // Two small tasks that together fit in 50 minutes
        createSampleTask(
                "Small task 1",
                TaskStatus.TODO,
                today,
                3,
                30
        );
        createSampleTask(
                "Small task 2",
                TaskStatus.TODO,
                today,
                3,
                20
        );

        String requestJson = """
            { "availableMinutes": 50 }
            """;

        mockMvc.perform(post("/api/tasks/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutes").value(50))
                .andExpect(jsonPath("$.remainingMinutes").value(0))
                .andExpect(jsonPath("$.tasks", hasSize(2)))
                .andExpect(jsonPath("$.tasks[*].title",
                        containsInAnyOrder("Small task 1", "Small task 2")))
                .andExpect(jsonPath("$.tasks[*].title",
                        not(hasItem("Big task"))));
    }

    @Test
    void planner_returnsEmptyListWhenNoTasksFit() throws Exception {
        // A single task that is too large
        createSampleTask(
                "Too big",
                TaskStatus.TODO,
                LocalDate.now(),
                5,
                120
        );

        String requestJson = """
            { "availableMinutes": 30 }
            """;

        mockMvc.perform(post("/api/tasks/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutes").value(0))
                .andExpect(jsonPath("$.remainingMinutes").value(30))
                .andExpect(jsonPath("$.tasks", hasSize(0)));
    }

    @Test
    void planner_rejectsInvalidAvailableMinutes() throws Exception {
        String requestJson = """
            { "availableMinutes": 0 }
            """;

        mockMvc.perform(post("/api/tasks/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("availableMinutes must be > 0"));
    }
}
