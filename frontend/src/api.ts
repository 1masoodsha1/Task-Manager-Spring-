import type { Task, TaskCreateOrUpdate } from './types';

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export async function fetchTasks(): Promise<Task[]> {
  const res = await fetch(`${API_BASE_URL}/api/tasks`);
  if (!res.ok) {
    throw new Error('Failed to fetch tasks');
  }
  return res.json();
}

export async function createTask(task: TaskCreateOrUpdate): Promise<Task> {
  const res = await fetch(`${API_BASE_URL}/api/tasks`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(task),
  });

  if (!res.ok) {
    throw new Error('Failed to create task');
  }

  return res.json();
}

export async function updateTask(
  id: number,
  task: TaskCreateOrUpdate,
): Promise<Task> {
  const res = await fetch(`${API_BASE_URL}/api/tasks/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(task),
  });

  if (!res.ok) {
    throw new Error('Failed to update task');
  }

  return res.json();
}

export async function deleteTask(id: number): Promise<void> {
  const res = await fetch(`${API_BASE_URL}/api/tasks/${id}`, {
    method: 'DELETE',
  });

  if (!res.ok) {
    throw new Error('Failed to delete task');
  }
}
