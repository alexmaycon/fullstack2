import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'

export interface TaskList {
  id: string
  name: string
  userId: string
  createdAt: string
  updatedAt: string
}

export const useTaskListsStore = defineStore('taskLists', () => {
  const items = ref<TaskList[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAll() {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get<TaskList[]>('/task-lists')
      items.value = data
    } catch (e) {
      error.value = extractMessage(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function create(name: string) {
    const { data } = await api.post<TaskList>('/task-lists', { name })
    items.value.push(data)
    return data
  }

  async function rename(id: string, name: string) {
    const { data } = await api.put<TaskList>(`/task-lists/${id}`, { name })
    const idx = items.value.findIndex((l) => l.id === id)
    if (idx >= 0) items.value[idx] = data
    return data
  }

  async function remove(id: string) {
    await api.delete(`/task-lists/${id}`)
    items.value = items.value.filter((l) => l.id !== id)
  }

  return { items, loading, error, fetchAll, create, rename, remove }
})

function extractMessage(e: unknown): string {
  const err = e as { response?: { data?: { message?: string } }; message?: string }
  return err.response?.data?.message || err.message || 'Erro inesperado'
}
