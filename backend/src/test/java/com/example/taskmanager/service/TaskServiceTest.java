package com.example.taskmanager.service;

import com.example.taskmanager.dto.WorkPlanResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void overdueHighPriorityChosenOverFutureLowPriority() {
        LocalDate today = LocalDate.now();

        Task overdue = new Task();
        overdue.setId(1L);
        overdue.setTitle("Overdue high priority");
        overdue.setStatus(TaskStatus.TODO);
        overdue.setDueDate(today.minusDays(1));
        overdue.setPriority(5);
        overdue.setEstimatedMinutes(60);

        Task future = new Task();
        future.setId(2L);
        future.setTitle("Future low priority");
        future.setStatus(TaskStatus.TODO);
        future.setDueDate(today.plusDays(7));
        future.setPriority(1);
        future.setEstimatedMinutes(60);

        when(taskRepository.findByStatus(TaskStatus.TODO))
                .thenReturn(List.of(overdue, future));

        WorkPlanResponse response = taskService.planWork(60);

        assertEquals(60, response.getTotalMinutes());
        assertEquals(0, response.getRemainingMinutes());
        assertEquals(1, response.getTasks().size());
        assertEquals("Overdue high priority", response.getTasks().get(0).getTitle());
    }

    @Test
    void smallerTasksChosenWhenLargeDoesNotFit() {
        LocalDate today = LocalDate.now();

        Task big = new Task();
        big.setId(1L);
        big.setTitle("Big task");
        big.setStatus(TaskStatus.TODO);
        big.setDueDate(today);
        big.setPriority(5);
        big.setEstimatedMinutes(90);

        Task small1 = new Task();
        small1.setId(2L);
        small1.setTitle("Small task 1");
        small1.setStatus(TaskStatus.TODO);
        small1.setDueDate(today);
        small1.setPriority(3);
        small1.setEstimatedMinutes(30);

        Task small2 = new Task();
        small2.setId(3L);
        small2.setTitle("Small task 2");
        small2.setStatus(TaskStatus.TODO);
        small2.setDueDate(today);
        small2.setPriority(3);
        small2.setEstimatedMinutes(20);

        when(taskRepository.findByStatus(TaskStatus.TODO))
                .thenReturn(List.of(big, small1, small2));

        WorkPlanResponse response = taskService.planWork(50);

        assertEquals(50, response.getTotalMinutes());
        assertEquals(0, response.getRemainingMinutes());
        assertEquals(2, response.getTasks().size());

        List<String> titles = response.getTasks()
                .stream()
                .map(Task::getTitle)
                .toList();

        assertTrue(titles.contains("Small task 1"));
        assertTrue(titles.contains("Small task 2"));
        assertFalse(titles.contains("Big task"));
    }
}