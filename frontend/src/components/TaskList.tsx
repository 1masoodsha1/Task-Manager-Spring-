import React from 'react';
import type { Task } from '../types';

interface TaskListProps {
  tasks: Task[];
  loading?: boolean;
  onEdit: (task: Task) => void;
  onDelete: (id: number) => Promise<void> | void;
  onRefresh?: () => void;
}

const TaskList: React.FC<TaskListProps> = ({
  tasks,
  loading = false,
  onEdit,
  onDelete,
  onRefresh,
}) => {
  return (
    <section className="card">
      <div className="card-header">
        <h2 className="card-title">Tasks</h2>
        {onRefresh && (
          <button className="btn btn-secondary" onClick={onRefresh} disabled={loading}>
            {loading ? 'Refreshing…' : 'Refresh'}
          </button>
        )}
      </div>

      {tasks.length === 0 ? (
        <p className="muted">No tasks yet. Create one above.</p>
      ) : (
        <ul className="task-list">
          {tasks.map((task) => (
            <li key={task.id} className="task-item">
              <div className="task-main">
                <div className="task-title">{task.title}</div>
                {task.description && (
                  <div className="task-desc">{task.description}</div>
                )}
                <div className="task-meta">
                  <span className={`status-badge status-${task.status.toLowerCase()}`}>
                    {task.status}
                  </span>
                  {task.dueDate && <span>Due: {task.dueDate}</span>}
                </div>
              </div>
              <div className="task-actions">
                <button
                  className="btn btn-small"
                  onClick={() => onEdit(task)}
                >
                  Edit
                </button>
                <button
                  className="btn btn-small btn-danger"
                  onClick={() => onDelete(task.id)}
                >
                  Delete
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
};

export default TaskList;
