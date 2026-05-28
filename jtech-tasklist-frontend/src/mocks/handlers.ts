import { http, HttpResponse, delay } from 'msw'

const API = '*/api/v1'

interface User {
  id: string
  name: string
  email: string
  password: string
}

interface TaskList {
  id: string
  name: string
  userId: string
  createdAt: string
  updatedAt: string
}

interface Task {
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

const STORAGE_USERS = 'jtech.mock.users'
const STORAGE_LISTS = 'jtech.mock.lists'
const STORAGE_TASKS = 'jtech.mock.tasks'
const STORAGE_SESSION = 'jtech.mock.session'

function load<T>(key: string, fallback: T): T {
  try {
    const raw = localStorage.getItem(key)
    return raw ? (JSON.parse(raw) as T) : fallback
  } catch {
    return fallback
  }
}

function save(key: string, value: unknown) {
  localStorage.setItem(key, JSON.stringify(value))
}

function uuid(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

function now(): string {
  return new Date().toISOString()
}

function expiresAt(minutes = 60): string {
  return new Date(Date.now() + minutes * 60_000).toISOString()
}

function tokenFor(userId: string): string {
  return `mock.${userId}.${Date.now().toString(36)}`
}

function currentUserId(authHeader: string | null): string | null {
  if (!authHeader?.startsWith('Bearer ')) return null
  const session = load<{ accessToken: string; userId: string } | null>(STORAGE_SESSION, null)
  if (!session) return null
  const token = authHeader.substring(7)
  return token === session.accessToken ? session.userId : null
}

function unauthorized() {
  return HttpResponse.json({ message: 'Não autorizado' }, { status: 401 })
}

function badRequest(message: string) {
  return HttpResponse.json({ message }, { status: 400 })
}

function notFound(message = 'Recurso não encontrado') {
  return HttpResponse.json({ message }, { status: 404 })
}

function buildSession(user: User) {
  const accessToken = tokenFor(user.id)
  const refreshToken = `refresh.${tokenFor(user.id)}`
  save(STORAGE_SESSION, { accessToken, refreshToken, userId: user.id })
  return {
    accessToken,
    refreshToken,
    tokenType: 'Bearer',
    expiresAt: expiresAt(),
    user: { id: user.id, name: user.name, email: user.email },
  }
}

export const handlers = [
  http.post(`${API}/auth/register`, async ({ request }) => {
    await delay(200)
    const body = (await request.json()) as { name?: string; email?: string; password?: string }
    if (!body?.name || !body?.email || !body?.password) {
      return badRequest('Nome, e-mail e senha são obrigatórios')
    }
    if (body.password.length < 6) {
      return badRequest('A senha deve ter no mínimo 6 caracteres')
    }
    const users = load<User[]>(STORAGE_USERS, [])
    if (users.some((u) => u.email.toLowerCase() === body.email!.toLowerCase())) {
      return HttpResponse.json({ message: 'E-mail já cadastrado' }, { status: 422 })
    }
    const user: User = {
      id: uuid(),
      name: body.name,
      email: body.email,
      password: body.password,
    }
    users.push(user)
    save(STORAGE_USERS, users)
    return HttpResponse.json(buildSession(user), { status: 201 })
  }),

  http.post(`${API}/auth/login`, async ({ request }) => {
    await delay(200)
    const body = (await request.json()) as { email?: string; password?: string }
    if (!body?.email || !body?.password) {
      return badRequest('E-mail e senha são obrigatórios')
    }
    const users = load<User[]>(STORAGE_USERS, [])
    let user = users.find(
      (u) => u.email.toLowerCase() === body.email!.toLowerCase() && u.password === body.password,
    )
    if (!user) {
      user = {
        id: uuid(),
        name: body.email.split('@')[0] || 'Usuário Mock',
        email: body.email,
        password: body.password,
      }
      users.push(user)
      save(STORAGE_USERS, users)
    }
    return HttpResponse.json(buildSession(user))
  }),

  http.post(`${API}/auth/refresh`, async ({ request }) => {
    await delay(100)
    const body = (await request.json()) as { refreshToken?: string }
    const session = load<{ accessToken: string; refreshToken: string; userId: string } | null>(
      STORAGE_SESSION,
      null,
    )
    if (!session || !body?.refreshToken || body.refreshToken !== session.refreshToken) {
      return unauthorized()
    }
    const users = load<User[]>(STORAGE_USERS, [])
    const user = users.find((u) => u.id === session.userId)
    if (!user) return unauthorized()
    return HttpResponse.json(buildSession(user))
  }),

  http.get(`${API}/task-lists`, ({ request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const lists = load<TaskList[]>(STORAGE_LISTS, []).filter((l) => l.userId === userId)
    return HttpResponse.json(lists)
  }),

  http.post(`${API}/task-lists`, async ({ request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const body = (await request.json()) as { name?: string }
    if (!body?.name?.trim()) return badRequest('Nome é obrigatório')
    const lists = load<TaskList[]>(STORAGE_LISTS, [])
    const item: TaskList = {
      id: uuid(),
      name: body.name.trim(),
      userId,
      createdAt: now(),
      updatedAt: now(),
    }
    lists.push(item)
    save(STORAGE_LISTS, lists)
    return HttpResponse.json(item, { status: 201 })
  }),

  http.put(`${API}/task-lists/:id`, async ({ params, request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const body = (await request.json()) as { name?: string }
    if (!body?.name?.trim()) return badRequest('Nome é obrigatório')
    const lists = load<TaskList[]>(STORAGE_LISTS, [])
    const idx = lists.findIndex((l) => l.id === params.id && l.userId === userId)
    if (idx < 0) return notFound('Lista não encontrada')
    lists[idx] = { ...lists[idx], name: body.name.trim(), updatedAt: now() }
    save(STORAGE_LISTS, lists)
    return HttpResponse.json(lists[idx])
  }),

  http.delete(`${API}/task-lists/:id`, ({ params, request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const lists = load<TaskList[]>(STORAGE_LISTS, [])
    const target = lists.find((l) => l.id === params.id && l.userId === userId)
    if (!target) return notFound('Lista não encontrada')
    save(STORAGE_LISTS, lists.filter((l) => l.id !== params.id))
    const tasks = load<Task[]>(STORAGE_TASKS, [])
    save(STORAGE_TASKS, tasks.filter((t) => t.taskListId !== params.id))
    return new HttpResponse(null, { status: 204 })
  }),

  http.get(`${API}/tasks`, ({ request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const url = new URL(request.url)
    const taskListId = url.searchParams.get('taskListId')
    const page = Number(url.searchParams.get('page') ?? '0')
    const size = Number(url.searchParams.get('size') ?? '20')
    let tasks = load<Task[]>(STORAGE_TASKS, []).filter((t) => t.userId === userId)
    if (taskListId) tasks = tasks.filter((t) => t.taskListId === taskListId)
    tasks.sort((a, b) => b.createdAt.localeCompare(a.createdAt))
    const totalElements = tasks.length
    const totalPages = Math.max(1, Math.ceil(totalElements / size))
    const content = tasks.slice(page * size, page * size + size)
    return HttpResponse.json({ content, totalElements, totalPages, number: page, size })
  }),

  http.get(`${API}/tasks/:id`, ({ params, request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const task = load<Task[]>(STORAGE_TASKS, []).find(
      (t) => t.id === params.id && t.userId === userId,
    )
    if (!task) return notFound('Tarefa não encontrada')
    return HttpResponse.json(task)
  }),

  http.post(`${API}/tasks`, async ({ request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const body = (await request.json()) as {
      taskListId?: string
      title?: string
      description?: string | null
      completed?: boolean
      dueDate?: string | null
    }
    if (!body?.title?.trim()) return badRequest('Título é obrigatório')
    if (!body?.taskListId) return badRequest('Lista é obrigatória')
    const lists = load<TaskList[]>(STORAGE_LISTS, [])
    if (!lists.some((l) => l.id === body.taskListId && l.userId === userId)) {
      return notFound('Lista não encontrada')
    }
    const tasks = load<Task[]>(STORAGE_TASKS, [])
    const task: Task = {
      id: uuid(),
      title: body.title.trim(),
      description: body.description ?? null,
      completed: !!body.completed,
      dueDate: body.dueDate ?? null,
      userId,
      taskListId: body.taskListId,
      createdAt: now(),
      updatedAt: now(),
    }
    tasks.push(task)
    save(STORAGE_TASKS, tasks)
    return HttpResponse.json(task, { status: 201 })
  }),

  http.put(`${API}/tasks/:id`, async ({ params, request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const body = (await request.json()) as {
      taskListId?: string
      title?: string
      description?: string | null
      completed?: boolean
      dueDate?: string | null
    }
    const tasks = load<Task[]>(STORAGE_TASKS, [])
    const idx = tasks.findIndex((t) => t.id === params.id && t.userId === userId)
    if (idx < 0) return notFound('Tarefa não encontrada')
    if (body?.title !== undefined && !body.title.trim()) {
      return badRequest('Título é obrigatório')
    }
    tasks[idx] = {
      ...tasks[idx],
      title: body.title?.trim() ?? tasks[idx].title,
      description: body.description ?? tasks[idx].description,
      completed: body.completed ?? tasks[idx].completed,
      dueDate: body.dueDate ?? tasks[idx].dueDate,
      taskListId: body.taskListId ?? tasks[idx].taskListId,
      updatedAt: now(),
    }
    save(STORAGE_TASKS, tasks)
    return HttpResponse.json(tasks[idx])
  }),

  http.patch(`${API}/tasks/:id/toggle`, ({ params, request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const tasks = load<Task[]>(STORAGE_TASKS, [])
    const idx = tasks.findIndex((t) => t.id === params.id && t.userId === userId)
    if (idx < 0) return notFound('Tarefa não encontrada')
    tasks[idx] = { ...tasks[idx], completed: !tasks[idx].completed, updatedAt: now() }
    save(STORAGE_TASKS, tasks)
    return HttpResponse.json(tasks[idx])
  }),

  http.delete(`${API}/tasks/:id`, ({ params, request }) => {
    const userId = currentUserId(request.headers.get('Authorization'))
    if (!userId) return unauthorized()
    const tasks = load<Task[]>(STORAGE_TASKS, [])
    const exists = tasks.some((t) => t.id === params.id && t.userId === userId)
    if (!exists) return notFound('Tarefa não encontrada')
    save(STORAGE_TASKS, tasks.filter((t) => t.id !== params.id))
    return new HttpResponse(null, { status: 204 })
  }),
]
