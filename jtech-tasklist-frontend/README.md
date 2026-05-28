# jtech-tasklist-frontend

Front-end do desafio fullstack **JTech Tasklist** — SPA construída com **Vue 3 + Vite + TypeScript + Vuetify 3**, consumindo a API REST `jtech-tasklist-backend`. Implementa autenticação JWT, gerenciamento de múltiplas listas e CRUD completo de tarefas com persistência via Pinia + interceptors Axios.

## Visão Geral da Arquitetura

Arquitetura modular orientada a camadas, separando claramente UI, estado, roteamento e acesso a dados:

- **Views** (`src/views`): páginas mapeadas pelas rotas (Login, Register, Dashboard).
- **Components** (`src/components`): componentes reutilizáveis (formulários, diálogos, listas).
- **Stores Pinia** (`src/stores`): estado global tipado por feature (`auth`, `taskLists`, `tasks`).
- **Services** (`src/services`): camada HTTP única (`api.ts`) com `axios` + interceptors de request (Bearer) e response (refresh token com fila).
- **Router** (`src/router`): rotas com guardas `requiresAuth` / `guestOnly`.
- **Plugins** (`src/plugins`): inicialização do Vuetify, tema e ícones MDI.

Fluxo de autenticação: a store `auth` persiste `accessToken`, `refreshToken` e dados do usuário em `localStorage`; o interceptor de resposta detecta `401`, tenta renovar via `/auth/refresh`, enfileira requisições concorrentes e reexecuta após sucesso.

## Stack Tecnológica

| Camada | Tecnologia | Justificativa |
|--------|------------|---------------|
| Framework | Vue 3.5 (Composition API + `<script setup>`) | Reatividade fina, melhor ergonomia com TS e composables. |
| Build | Vite 7 | Dev server instantâneo, HMR e build otimizado com Rollup. |
| Linguagem | TypeScript 5.8 | Tipagem forte de DTOs, stores e props. |
| Roteamento | vue-router 4 | Padrão oficial; guardas de rota baseadas em store de auth. |
| Estado | Pinia 3 (setup stores) | API simples, suporte nativo a TS, devtools. |
| UI | Vuetify 3 + Material Design Icons | Componentes Material prontos, acessíveis e temáveis. |
| HTTP | Axios 1 | Interceptors prontos para JWT/refresh. |
| Testes | Vitest 3 + @vue/test-utils + jsdom | Stack oficial do ecossistema Vite. |
| Lint/Format | ESLint 9 + Prettier 3 | Padronização do código. |

## Como Rodar Localmente

### Pré-requisitos

- Node.js `^20.19` ou `>=22.12`
- API backend rodando em <http://localhost:8080> (ver `../jtech-tasklist-backend/README.md`).

### Instalação

```sh
npm install
```

### Variáveis de ambiente

Criar `.env` (ou `.env.local`) na raiz do projeto:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

### Execução em desenvolvimento

```sh
npm run dev
```

App disponível em <http://localhost:5173>.

### Build de produção

```sh
npm run build
npm run preview
```

## Como Rodar os Testes

### Testes unitários (Vitest)

```sh
npm run test:unit
```

### Verificação de tipos

```sh
npm run type-check
```

### Lint

```sh
npm run lint
```

### Formatação

```sh
npm run format
```

## Estrutura de Pastas Detalhada

```
jtech-tasklist-frontend/
├── index.html                  HTML root
├── vite.config.ts              Vite + plugin Vue + vite-plugin-vuetify
├── vitest.config.ts            Configuração de testes
├── tsconfig*.json              Configurações TypeScript (app/node/vitest)
├── eslint.config.ts            Regras de lint
├── public/                     Assets estáticos servidos cru
└── src/
    ├── main.ts                 Bootstrap (Pinia, Router, Vuetify)
    ├── App.vue                 Shell com AppBar e <RouterView/>
    ├── assets/                 CSS base e imagens
    ├── plugins/
    │   └── vuetify.ts          createVuetify (tema + ícones MDI)
    ├── router/
    │   └── index.ts            Rotas + guardas requiresAuth/guestOnly
    ├── services/
    │   └── api.ts              Axios + interceptors (Bearer/refresh)
    ├── stores/
    │   ├── auth.ts             Login, register, refresh, logout
    │   ├── taskLists.ts        CRUD de listas
    │   └── tasks.ts            CRUD paginado de tarefas
    ├── views/
    │   ├── LoginView.vue
    │   ├── RegisterView.vue
    │   └── DashboardView.vue   Listas + tarefas + diálogos
    └── components/
        ├── HelloWorld.vue
        ├── icons/
        └── __tests__/          Specs Vitest
```

## Decisões Técnicas Aprofundadas

- **Composition API + `<script setup>`**: reduz boilerplate e permite composables tipados para isolar lógica reutilizável (auth, paginação).
- **Pinia setup stores**: estado por feature, evitando uma única store monolítica; cada store expõe apenas o necessário (encapsulamento).
- **Axios centralizado em `services/api.ts`**: única fonte da verdade para `baseURL`, headers e tratamento de `401`. O refresh usa fila para evitar múltiplas chamadas concorrentes ao endpoint `/auth/refresh`.
- **Guards de rota baseadas em store**: `requiresAuth` redireciona para `/login`; `guestOnly` impede usuário autenticado de acessar `/login` e `/register`.
- **Vuetify 3 com `vite-plugin-vuetify`**: ativa auto-import de componentes e tree-shaking, mantendo o bundle enxuto em produção.
- **Tema customizado** (`primary: #00805C`, `secondary: #1976D2`) aplicado via `createVuetify`, alinhado à identidade JTech.
- **Validação no cliente**: regras declarativas via `v-text-field :rules` (email regex, tamanho mínimo de senha 6 — igual ao backend).
- **Resiliência de storage**: `loadUser()` na store `auth` ignora valores corrompidos (`"undefined"`, JSON inválido) ao reidratar o estado.
- **TypeScript estrito**: DTOs alinhados aos contratos do backend; respostas paginadas tipadas (`Page<T>`).
- **Separação clara entre UI e domínio**: views nunca chamam Axios diretamente; sempre via store.

## Melhorias e Roadmap

- **Cobertura de testes**: ampliar specs para stores e views críticas (auth, dashboard) com mocks de Axios.
- **Testes E2E** com Playwright/Cypress cobrindo fluxos login → criar lista → criar tarefa.
- **Acessibilidade (a11y)**: revisão completa de foco, ARIA e contraste em todas as telas.
- **Internacionalização (i18n)** com `vue-i18n`, suportando pt-BR e en-US.
- **PWA** com cache offline para visualização de listas/tarefas sem conexão.
- **Lazy-loading de rotas** e code splitting por feature.
- **Skeleton loaders** e estados de erro padronizados via componente compartilhado.
- **Drag & drop** para reordenar tarefas (`vuedraggable`).
- **Pipeline CI** (GitHub Actions) executando `lint`, `type-check`, `test:unit` e `build` em cada PR.
- **Dark mode** alternável e persistido na preferência do usuário.

---

### Recursos auxiliares

- [Vite Configuration Reference](https://vite.dev/config/)
- [Vue 3 Docs](https://vuejs.org/)
- [Vuetify 3 Docs](https://vuetifyjs.com/)
- IDE recomendada: [VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (desabilitar Vetur).
