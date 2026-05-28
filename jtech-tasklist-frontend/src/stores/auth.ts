import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import api, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY } from '@/services/api'

export interface AuthUser {
  id: string
  name: string
  email: string
}

interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresAt: string
  user: AuthUser
}

const USER_KEY = 'jtech.user'

function loadUser(): AuthUser | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw || raw === 'undefined' || raw === 'null') return null
  try {
    return JSON.parse(raw) as AuthUser
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(localStorage.getItem(ACCESS_TOKEN_KEY))
  const refreshToken = ref<string | null>(localStorage.getItem(REFRESH_TOKEN_KEY))
  const user = ref<AuthUser | null>(loadUser())

  const isAuthenticated = computed(() => !!accessToken.value)

  function persist(data: AuthResponse) {
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    localStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken)
    localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
    localStorage.setItem(USER_KEY, JSON.stringify(data.user))
  }

  async function login(email: string, password: string) {
    const { data } = await api.post<AuthResponse>('/auth/login', { email, password })
    persist(data)
  }

  async function register(name: string, email: string, password: string) {
    const { data } = await api.post<AuthResponse>('/auth/register', { name, email, password })
    persist(data)
  }

  function logout() {
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { accessToken, refreshToken, user, isAuthenticated, login, register, logout }
})
