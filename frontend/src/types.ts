export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface Task {
    id: number;
    title: string;
    description?: string | null;
    status: TaskStatus;
    dueDate?: string | null; // ISO date string, e.g. "2025-11-20"
}

export type TaskCreateOrUpdate = Omit<Task, 'id'>;