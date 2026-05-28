import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'

export interface Task {
  id: string
  title: string
  description: string | null
  completed: boolean
  dueDate: string | null
  userId: string
  taskListId: string
  createdAt: string
  updatedAt: string
}

interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface TaskInput {
  taskListId?: string
  title: string
  description?: string | null
  completed?: boolean
  dueDate?: string | null
}

export const useTasksStore = defineStore('tasks', () => {
  const items = ref<Task[]>([])
  const totalElements = ref(0)
  const totalPages = ref(0)
  const page = ref(0)
  const size = ref(20)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAll(opts: { taskListId?: string; page?: number; size?: number } = {}) {
    loading.value = true
    error.value = null
    try {
      const params: Record<string, string | number> = {
        page: opts.page ?? page.value,
        size: opts.size ?? size.value,
      }
      if (opts.taskListId) params.taskListId = opts.taskListId
      const { data } = await api.get<Page<Task>>('/tasks', { params })
      items.value = data.content
      totalElements.value = data.totalElements
      totalPages.value = data.totalPages
      page.value = data.number
      size.value = data.size
    } catch (e) {
      error.value = extractMessage(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function create(input: TaskInput) {
    const { data } = await api.post<Task>('/tasks', input)
    items.value.unshift(data)
    return data
  }

  async function update(id: string, input: TaskInput) {
    const { data } = await api.put<Task>(`/tasks/${id}`, input)
    const idx = items.value.findIndex((t) => t.id === id)
    if (idx >= 0) items.value[idx] = data
    return data
  }

  async function toggle(task: Task) {
    return update(task.id, {
      taskListId: task.taskListId,
      title: task.title,
      description: task.description,
      completed: !task.completed,
      dueDate: task.dueDate,
    })
  }

  async function remove(id: string) {
    await api.delete(`/tasks/${id}`)
    items.value = items.value.filter((t) => t.id !== id)
  }

  return {
    items, totalElements, totalPages, page, size, loading, error,
    fetchAll, create, update, toggle, remove,
  }
})

function extractMessage(e: unknown): string {
  const err = e as { response?: { data?: { message?: string } }; message?: string }
  return err.response?.data?.message || err.message || 'Erro inesperado'
}
