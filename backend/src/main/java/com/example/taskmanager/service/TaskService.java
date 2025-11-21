package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updated) {
        Task existing = getTask(id);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setDueDate(updated.getDueDate());

        return taskRepository.save(existing);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
