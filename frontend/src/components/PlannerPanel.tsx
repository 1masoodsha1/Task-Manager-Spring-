import { useState } from 'react';
import type { WorkPlanResponse } from '../types';

interface PlannerPanelProps {
  onPlan: (availableMinutes: number) => void | Promise<void>;
  planResult: WorkPlanResponse | null;
}

const PlannerPanel: React.FC<PlannerPanelProps> = ({
  onPlan,
  planResult,
}) => {
  const [minutes, setMinutes] = useState<number | ''>(60);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const value = Number(minutes);
    if (!value || value <= 0) {
      alert('Please enter a positive number of minutes.');
      return;
    }
    void onPlan(value);
  };

  return (
    <div>
      <form className="form" onSubmit={handleSubmit}>
        <label className="form-label">
          Available minutes
          <input
            type="number"
            min={1}
            value={minutes}
            onChange={(e) =>
              setMinutes(
                e.target.value === '' ? '' : Number(e.target.value)
              )
            }
          />
        </label>
        <div className="form-actions">
          <button className="btn btn-primary" type="submit">
            Plan Work
          </button>
        </div>
      </form>

      {planResult && (
        <div className="planner-result">
          <div className="planner-summary">
            <div>
              <span className="summary-label">Planned time:</span>{' '}
              {planResult.totalMinutes} min
            </div>
            <div>
              <span className="summary-label">Remaining:</span>{' '}
              {planResult.remainingMinutes} min
            </div>
          </div>

          {planResult.tasks && planResult.tasks.length > 0 ? (
            <ul className="planner-task-list">
              {planResult.tasks.map((t) => (
                <li key={t.id} className="planner-task-item">
                  <div className="planner-task-title">{t.title}</div>
                  <div className="planner-task-meta">
                    <span>Priority: {t.priority}</span>
                    <span>Est: {t.estimatedMinutes} min</span>
                    {t.dueDate && <span>Due: {t.dueDate}</span>}
                  </div>
                  {t.description && (
                    <div className="planner-task-desc">
                      {t.description}
                    </div>
                  )}
                </li>
              ))}
            </ul>
          ) : (
            <div className="muted">
              No tasks fit in this time block. Try adjusting your available
              minutes or adding TODO tasks.
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PlannerPanel;