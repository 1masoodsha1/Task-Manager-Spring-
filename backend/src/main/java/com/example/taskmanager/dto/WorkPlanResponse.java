package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Task;

import java.util.List;

public class WorkPlanResponse {
    private int totalMinutes;
    private int remainingMinutes;
    private List<Task> tasks;

    public WorkPlanResponse() {
    }

    public WorkPlanResponse(int totalMinutes, int remainingMinutes, List<Task> tasks) {
        this.totalMinutes = totalMinutes;
        this.remainingMinutes = remainingMinutes;
        this.tasks = tasks;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setRemainingMinutes(int remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
