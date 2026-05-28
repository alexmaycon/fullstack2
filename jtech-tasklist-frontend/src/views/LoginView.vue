<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const email = ref('')
const password = ref('')
const error = ref<string | null>(null)
const loading = ref(false)
const showPassword = ref(false)

const emailRules = [
  (v: string) => !!v || 'Informe o e-mail',
  (v: string) => /.+@.+\..+/.test(v) || 'E-mail inválido',
]
const passwordRules = [
  (v: string) => !!v || 'Informe a senha',
  (v: string) => v.length >= 6 || 'Mínimo de 6 caracteres',
]

async function submit() {
  error.value = null
  loading.value = true
  try {
    await auth.login(email.value, password.value)
    router.push('/')
  } catch (e) {
    const err = e as { response?: { data?: { message?: string } } }
    error.value = err.response?.data?.message || 'Falha ao autenticar'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-container class="fill-height" max-width="480">
    <v-card class="pa-6 mx-auto w-100" elevation="3">
      <v-card-title class="text-h5 mb-2">Entrar</v-card-title>
      <v-form @submit.prevent="submit">
        <v-text-field
          v-model="email"
          label="E-mail"
          type="email"
          autocomplete="email"
          prepend-inner-icon="mdi-email-outline"
          :rules="emailRules"
          required
        />
        <v-text-field
          v-model="password"
          label="Senha"
          :type="showPassword ? 'text' : 'password'"
          autocomplete="current-password"
          prepend-inner-icon="mdi-lock-outline"
          :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showPassword = !showPassword"
          :rules="passwordRules"
          required
        />
        <v-alert v-if="error" type="error" variant="tonal" density="compact" class="mb-3">
          {{ error }}
        </v-alert>
        <v-btn type="submit" color="primary" block size="large" :loading="loading">Entrar</v-btn>
      </v-form>
      <v-card-text class="text-center pt-4">
        Ainda não tem conta?
        <RouterLink to="/register">Cadastre-se</RouterLink>
      </v-card-text>
    </v-card>
  </v-container>
</template>
