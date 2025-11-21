import React, { useState, useEffect } from 'react';
import type { Task, TaskCreateOrUpdate, TaskStatus } from '../types';

interface TaskFormProps {
  initialTask?: Task | null;
  onSubmit: (task: TaskCreateOrUpdate) => Promise<void> | void;
  onCancelEdit?: () => void;
}

const defaultForm: TaskCreateOrUpdate = {
  title: '',
  description: '',
  status: 'TODO',
  dueDate: '',
};

const TaskForm: React.FC<TaskFormProps> = ({
  initialTask,
  onSubmit,
  onCancelEdit,
}) => {
  const [form, setForm] = useState<TaskCreateOrUpdate>(defaultForm);
  const [submitting, setSubmitting] = useState(false);

  // Refill form when switching between "create" and "edit"
  useEffect(() => {
    if (initialTask) {
      setForm({
        title: initialTask.title ?? '',
        description: initialTask.description ?? '',
        status: initialTask.status,
        dueDate: initialTask.dueDate ?? '',
      });
    } else {
      setForm(defaultForm);
    }
  }, [initialTask?.id]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.title.trim()) return;

    try {
      setSubmitting(true);
      await onSubmit({
        ...form,
        title: form.title.trim(),
        description: form.description?.trim() || '',
        status: form.status as TaskStatus,
        dueDate: form.dueDate || null,
      });
      if (!initialTask) {
        setForm(defaultForm);
      }
    } finally {
      setSubmitting(false);
    }
  };

  const isEditing = !!initialTask;

  return (
    <section className="card">
      <h2 className="card-title">{isEditing ? 'Edit Task' : 'Create Task'}</h2>
      <form className="form" onSubmit={handleSubmit}>
        <label className="form-label">
          Title
          <input
            name="title"
            value={form.title}
            onChange={handleChange}
            placeholder="e.g. Fix login bug"
            required
          />
        </label>

        <label className="form-label">
          Description
          <textarea
            name="description"
            value={form.description ?? ''}
            onChange={handleChange}
            placeholder="Optional details about the task"
          />
        </label>

        <div className="form-row">
          <label className="form-label">
            Status
            <select
              name="status"
              value={form.status}
              onChange={handleChange}
            >
              <option value="TODO">TODO</option>
              <option value="IN_PROGRESS">IN PROGRESS</option>
              <option value="DONE">DONE</option>
            </select>
          </label>

          <label className="form-label">
            Due date
            <input
              type="date"
              name="dueDate"
              value={form.dueDate ?? ''}
              onChange={handleChange}
            />
          </label>
        </div>

        <div className="form-actions">
          <button className="btn btn-primary" type="submit" disabled={submitting}>
            {submitting ? 'Saving…' : isEditing ? 'Save changes' : 'Create task'}
          </button>
          {isEditing && onCancelEdit && (
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onCancelEdit}
            >
              Cancel
            </button>
          )}
        </div>
      </form>
    </section>
  );
};

export default TaskForm;
