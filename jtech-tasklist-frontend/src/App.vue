<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <v-app>
    <v-app-bar color="primary" density="comfortable" flat>
      <v-app-bar-title>
        <v-icon icon="mdi-check-circle-outline" class="mr-2" />
        JTech Tasklist
      </v-app-bar-title>
      <v-spacer />
      <template v-if="auth.isAuthenticated">
        <span class="text-body-2 mr-3">{{ auth.user?.name }}</span>
        <v-btn variant="text" prepend-icon="mdi-logout" @click="logout">Sair</v-btn>
      </template>
    </v-app-bar>
    <v-main>
      <RouterView />
    </v-main>
  </v-app>
</template>
