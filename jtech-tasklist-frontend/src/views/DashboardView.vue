<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useTaskListsStore, type TaskList } from '@/stores/taskLists'
import { useTasksStore, type Task } from '@/stores/tasks'

const listsStore = useTaskListsStore()
const tasksStore = useTasksStore()

const selectedListId = ref<string | null>(null)
const newListName = ref('')
const showNewTaskDialog = ref(false)
const showEditTaskDialog = ref(false)
const showRenameDialog = ref(false)
const renameListId = ref<string | null>(null)
const renameListName = ref('')

const newTask = ref<{ title: string; description: string; dueDate: string }>({
  title: '',
  description: '',
  dueDate: '',
})

const editingTask = ref<Task | null>(null)

const snackbar = ref<{ show: boolean; text: string; color: string }>({
  show: false,
  text: '',
  color: 'error',
})

function notify(text: string, color = 'error') {
  snackbar.value = { show: true, text, color }
}

function handleError(e: unknown) {
  const err = e as { response?: { data?: { message?: string } } }
  notify(err.response?.data?.message || 'Erro inesperado')
}

async function loadLists() {
  try {
    await listsStore.fetchAll()
    if (!selectedListId.value && listsStore.items.length > 0) {
      selectedListId.value = listsStore.items[0].id
    }
  } catch (e) { handleError(e) }
}

async function loadTasks() {
  try {
    await tasksStore.fetchAll({ taskListId: selectedListId.value ?? undefined, page: 0 })
  } catch (e) { handleError(e) }
}

async function createList() {
  if (!newListName.value.trim()) return
  try {
    const created = await listsStore.create(newListName.value.trim())
    newListName.value = ''
    selectedListId.value = created.id
    notify('Lista criada', 'success')
  } catch (e) { handleError(e) }
}

function openRename(list: TaskList) {
  renameListId.value = list.id
  renameListName.value = list.name
  showRenameDialog.value = true
}

async function saveRename() {
  if (!renameListId.value || !renameListName.value.trim()) return
  try {
    await listsStore.rename(renameListId.value, renameListName.value.trim())
    showRenameDialog.value = false
    notify('Lista renomeada', 'success')
  } catch (e) { handleError(e) }
}

async function deleteList(list: TaskList) {
  if (!confirm(`Excluir a lista "${list.name}"?`)) return
  try {
    await listsStore.remove(list.id)
    if (selectedListId.value === list.id) {
      selectedListId.value = listsStore.items[0]?.id ?? null
    }
    notify('Lista excluída', 'success')
  } catch (e) { handleError(e) }
}

function openNewTask() {
  newTask.value = { title: '', description: '', dueDate: '' }
  showNewTaskDialog.value = true
}

async function createTask() {
  if (!selectedListId.value || !newTask.value.title.trim()) return
  try {
    await tasksStore.create({
      taskListId: selectedListId.value,
      title: newTask.value.title.trim(),
      description: newTask.value.description.trim() || null,
      dueDate: newTask.value.dueDate ? new Date(newTask.value.dueDate).toISOString() : null,
      completed: false,
    })
    showNewTaskDialog.value = false
    notify('Tarefa adicionada', 'success')
  } catch (e) { handleError(e) }
}

async function toggleTask(task: Task) {
  try { await tasksStore.toggle(task) } catch (e) { handleError(e) }
}

function openEditTask(task: Task) {
  editingTask.value = {
    ...task,
    dueDate: task.dueDate ? task.dueDate.substring(0, 16) : null,
  }
  showEditTaskDialog.value = true
}

async function saveEditTask() {
  if (!editingTask.value) return
  try {
    await tasksStore.update(editingTask.value.id, {
      taskListId: editingTask.value.taskListId,
      title: editingTask.value.title,
      description: editingTask.value.description,
      completed: editingTask.value.completed,
      dueDate: editingTask.value.dueDate ? new Date(editingTask.value.dueDate).toISOString() : null,
    })
    showEditTaskDialog.value = false
    editingTask.value = null
    notify('Tarefa atualizada', 'success')
  } catch (e) { handleError(e) }
}

async function deleteTask(task: Task) {
  if (!confirm(`Excluir a tarefa "${task.title}"?`)) return
  try {
    await tasksStore.remove(task.id)
    notify('Tarefa excluída', 'success')
  } catch (e) { handleError(e) }
}

async function changePage(next: number) {
  if (next < 1 || next > tasksStore.totalPages) return
  try {
    await tasksStore.fetchAll({ taskListId: selectedListId.value ?? undefined, page: next - 1 })
  } catch (e) { handleError(e) }
}

watch(selectedListId, () => { loadTasks() })

onMounted(async () => {
  await loadLists()
  await loadTasks()
})
</script>

<template>
  <v-container fluid class="pa-4">
    <v-row>
      <v-col cols="12" md="4" lg="3">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon icon="mdi-format-list-bulleted" class="mr-2" />
            Minhas Listas
          </v-card-title>
          <v-card-text>
            <v-form @submit.prevent="createList" class="d-flex ga-2 mb-3">
              <v-text-field
                v-model="newListName"
                label="Nova lista"
                density="compact"
                hide-details
                variant="outlined"
              />
              <v-btn type="submit" color="primary" icon="mdi-plus" />
            </v-form>
            <v-list v-if="listsStore.items.length" density="compact" nav>
              <v-list-item
                v-for="list in listsStore.items"
                :key="list.id"
                :active="list.id === selectedListId"
                @click="selectedListId = list.id"
                :title="list.name"
                prepend-icon="mdi-folder-outline"
              >
                <template #append>
                  <v-btn
                    icon="mdi-pencil"
                    size="x-small"
                    variant="text"
                    @click.stop="openRename(list)"
                  />
                  <v-btn
                    icon="mdi-delete"
                    size="x-small"
                    variant="text"
                    color="error"
                    @click.stop="deleteList(list)"
                  />
                </template>
              </v-list-item>
            </v-list>
            <v-alert v-else type="info" variant="tonal" density="compact">
              Nenhuma lista. Crie a primeira acima.
            </v-alert>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="8" lg="9">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon icon="mdi-checkbox-marked-outline" class="mr-2" />
            Tarefas
            <v-spacer />
            <v-btn
              v-if="selectedListId"
              color="primary"
              prepend-icon="mdi-plus"
              @click="openNewTask"
            >
              Nova tarefa
            </v-btn>
          </v-card-title>
          <v-card-text>
            <v-alert v-if="!selectedListId" type="info" variant="tonal" density="compact">
              Selecione ou crie uma lista para gerenciar tarefas.
            </v-alert>
            <template v-else>
              <v-list v-if="tasksStore.items.length" lines="two">
                <v-list-item
                  v-for="task in tasksStore.items"
                  :key="task.id"
                  :class="{ 'text-disabled': task.completed }"
                >
                  <template #prepend>
                    <v-checkbox-btn
                      :model-value="task.completed"
                      @update:model-value="toggleTask(task)"
                    />
                  </template>
                  <v-list-item-title :class="{ 'text-decoration-line-through': task.completed }">
                    {{ task.title }}
                  </v-list-item-title>
                  <v-list-item-subtitle>
                    <span v-if="task.description">{{ task.description }}</span>
                    <span v-if="task.dueDate" class="ml-2 text-caption">
                      <v-icon size="x-small" icon="mdi-calendar-clock" />
                      {{ new Date(task.dueDate).toLocaleString() }}
                    </span>
                  </v-list-item-subtitle>
                  <template #append>
                    <v-btn icon="mdi-pencil" size="small" variant="text" @click="openEditTask(task)" />
                    <v-btn icon="mdi-delete" size="small" variant="text" color="error" @click="deleteTask(task)" />
                  </template>
                </v-list-item>
              </v-list>
              <v-alert v-else-if="!tasksStore.loading" type="info" variant="tonal" density="compact">
                Nenhuma tarefa nesta lista.
              </v-alert>
              <v-progress-linear v-if="tasksStore.loading" indeterminate class="mt-2" />
              <v-pagination
                v-if="tasksStore.totalPages > 1"
                :model-value="tasksStore.page + 1"
                :length="tasksStore.totalPages"
                @update:model-value="changePage"
                class="mt-4"
              />
            </template>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="showNewTaskDialog" max-width="520">
      <v-card>
        <v-card-title>Nova tarefa</v-card-title>
        <v-card-text>
          <v-text-field v-model="newTask.title" label="Título" required maxlength="180" counter />
          <v-textarea v-model="newTask.description" label="Descrição" rows="3" maxlength="2000" counter />
          <v-text-field v-model="newTask.dueDate" label="Prazo" type="datetime-local" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showNewTaskDialog = false">Cancelar</v-btn>
          <v-btn color="primary" @click="createTask">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showEditTaskDialog" max-width="520">
      <v-card v-if="editingTask">
        <v-card-title>Editar tarefa</v-card-title>
        <v-card-text>
          <v-text-field v-model="editingTask.title" label="Título" required maxlength="180" />
          <v-textarea v-model="editingTask.description" label="Descrição" rows="3" maxlength="2000" />
          <v-text-field v-model="editingTask.dueDate" label="Prazo" type="datetime-local" />
          <v-select
            v-model="editingTask.taskListId"
            :items="listsStore.items"
            item-title="name"
            item-value="id"
            label="Lista"
          />
          <v-switch v-model="editingTask.completed" label="Concluída" color="primary" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showEditTaskDialog = false">Cancelar</v-btn>
          <v-btn color="primary" @click="saveEditTask">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showRenameDialog" max-width="420">
      <v-card>
        <v-card-title>Renomear lista</v-card-title>
        <v-card-text>
          <v-text-field v-model="renameListName" label="Nome" required />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showRenameDialog = false">Cancelar</v-btn>
          <v-btn color="primary" @click="saveRename">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3500" location="top">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>
