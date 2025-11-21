import React, { useEffect, useState } from 'react';
import './styles.css';
import type { Task, TaskCreateOrUpdate } from './types';
import { fetchTasks, createTask, updateTask, deleteTask } from './api';
import TaskForm from './components/TaskForm';
import TaskList from './components/TaskList';

const App: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [error, setError] = useState<string | null>(null);

  const loadTasks = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await fetchTasks();
      setTasks(data);
    } catch (err: any) {
      setError(err?.message ?? 'Failed to load tasks');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadTasks();
  }, []);

  const handleCreateOrUpdate = async (taskInput: TaskCreateOrUpdate) => {
    setError(null);
    try {
      if (editingTask) {
        const updated = await updateTask(editingTask.id, taskInput);
        setTasks((prev) =>
          prev.map((t) => (t.id === updated.id ? updated : t)),
        );
        setEditingTask(null);
      } else {
        const created = await createTask(taskInput);
        setTasks((prev) => [...prev, created]);
      }
    } catch (err: any) {
      setError(err?.message ?? 'Failed to save task');
    }
  };

  const handleDelete = async (id: number) => {
    setError(null);
    try {
      await deleteTask(id);
      setTasks((prev) => prev.filter((t) => t.id !== id));
      if (editingTask && editingTask.id === id) {
        setEditingTask(null);
      }
    } catch (err: any) {
      setError(err?.message ?? 'Failed to delete task');
    }
  };

  return (
    <div className="page">
      <header className="page-header">
        <h1>Task Manager</h1>
        <p className="subtitle">
          Manage tasks: create, view, update, and delete them.
        </p>
      </header>

      <main className="layout">
        <div className="layout-column">
          <TaskForm
            initialTask={editingTask}
            onSubmit={handleCreateOrUpdate}
            onCancelEdit={() => setEditingTask(null)}
          />
          {error && <div className="alert alert-error">{error}</div>}
        </div>

        <div className="layout-column">
          <TaskList
            tasks={tasks}
            loading={loading}
            onEdit={setEditingTask}
            onDelete={handleDelete}
            onRefresh={loadTasks}
          />
        </div>
      </main>
    </div>
  );
};

export default App;
