# Desafio Técnico Fullstack2 - JTech

## Sistema TODO List Multi-usuário com Arquitetura Avançada

---

## 🚀 Solução Implementada

Este repositório contém os dois projetos da solução:

| Projeto | Stack | Descrição |
|---------|-------|-----------|
| [`jtech-tasklist-backend`](./jtech-tasklist-backend) | Java 21 · Spring Boot 3.5 · PostgreSQL · JWT · Liquibase | API REST hexagonal com autenticação JWT e CRUD de listas/tarefas |
| [`jtech-tasklist-frontend`](./jtech-tasklist-frontend) | Vue 3 · Vite · TypeScript · Pinia · Vuetify 3 · MSW | SPA Material Design consumindo a API (ou MSW em modo mock) |

Cada projeto possui um `README.md` próprio com detalhes adicionais (estrutura, decisões técnicas e roadmap).

### Pré-requisitos

- **Java 21+** e **Gradle Wrapper** (incluso) — para o back-end.
- **Docker** — para subir o PostgreSQL local.
- **Node.js `^20.19` ou `>=22.12`** e **npm** — para o front-end.
- **Git**.

### Clonando

```sh
git clone <url-do-repo> fullstack2
cd fullstack2
```

---

### 1. Back-end (`jtech-tasklist-backend`)

#### 1.1. Subir o PostgreSQL

```sh
cd jtech-tasklist-backend/composer
docker compose up -d
```

Provisiona `postgres:16-alpine` em `localhost:5432` (db `tasklist_db`, usuário/senha `postgres`).

#### 1.2. Build

```sh
cd jtech-tasklist-backend
./gradlew clean build
```

(`gradlew.bat` no Windows). Gera o `.jar` em `build/libs/` e executa toda a suíte de testes + relatório Jacoco (`build/reports/jacoco/test/html/index.html`).

#### 1.3. Executar

Em desenvolvimento (hot reload):

```sh
./gradlew bootRun
```

Ou via `.jar` gerado:

```sh
java -jar build/libs/jtech-tasklist-*.jar
```

API em <http://localhost:8080> · Swagger UI em <http://localhost:8080/doc/tasklist/v1/swagger-ui/index.html>

O **Liquibase** executa as migrações automaticamente no primeiro start.

#### 1.4. Variáveis de ambiente (opcionais)

| Variável | Default |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tasklist_db` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` |
| `JWT_SECRET` | definido em `application.yml` |
| `JWT_EXPIRATION` | tempo do access token em ms |

---

### 2. Front-end (`jtech-tasklist-frontend`)

#### 2.1. Instalar dependências

```sh
cd jtech-tasklist-frontend
npm install
```

#### 2.2. Configurar `.env`

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_USE_MOCK=false
```

#### 2.3. Executar em desenvolvimento

```sh
npm run dev
```

Disponível em <http://localhost:5173>.


#### 2.4. Build de produção

```sh
npm run build
npm run preview
```

---

### 3. Modo MSW (rodar o front-end **sem o back-end**)

O front-end suporta um modo de **autenticação e dados mockados** via [Mock Service Worker](https://mswjs.io/), atendendo ao requisito original do desafio de "autenticação simulada — qualquer combinação válida de usuário/senha redireciona para a aplicação".

#### Como ativar

1. Edite [`jtech-tasklist-frontend/.env`](./jtech-tasklist-frontend/.env):

   ```env
   VITE_USE_MOCK=true
   ```

2. Rode o front normalmente:

   ```sh
   npm run dev
   ```

3. No browser, faça **Ctrl + F5** para registrar o Service Worker. O console mostrará:

   ```
   [MSW] Mocking enabled.
   ```

#### O que muda no modo mock

- **Login**: aceita **qualquer e-mail e senha** (mínimo 6 caracteres). Se o e-mail ainda não existir, o usuário é criado on the fly.
- **Registro**: funciona com validação (nome, e-mail único, senha ≥ 6).
- **Listas e tarefas**: CRUD completo com persistência em `localStorage` por usuário.
- **JWT**: tokens mock + refresh-token-flow funcional (o interceptor do Axios é exercitado de verdade).
- **Back-end não precisa estar rodando.**

#### Para limpar o estado mock

No DevTools do browser:

```js
localStorage.clear()
```

Para voltar ao back-end real, basta setar `VITE_USE_MOCK=false` e reiniciar o `npm run dev`.

---

### 4. Testes

#### Back-end

```sh
cd jtech-tasklist-backend
./gradlew test                 # unitários + integração
./gradlew jacocoTestReport     # relatório de cobertura
```

Cobertura atual: **≥ 80% de instruções**. Relatório em `build/reports/jacoco/test/html/index.html`.

#### Front-end

```sh
cd jtech-tasklist-frontend
npm run test:unit              # Vitest
npm run type-check             # vue-tsc
npm run lint                   # ESLint
```

---

### 5. Fluxo recomendado de validação

1. Suba o Postgres (`docker compose up -d`).
2. Inicie o back-end (`./gradlew bootRun`).
3. Inicie o front-end com `VITE_USE_MOCK=false` (`npm run dev`).
4. Acesse <http://localhost:5173>, cadastre um usuário, faça login e teste o CRUD de listas/tarefas.
5. (Opcional) Alterne para `VITE_USE_MOCK=true` para validar a aplicação isoladamente, sem dependências de infraestrutura.

---

## 📋 Especificação Original do Desafio

### Contextualização e Objetivo

A **JTech** busca desenvolvedores frontend experientes capazes de construir aplicações robustas e escaláveis com arquitetura bem definida. Este desafio avalia sua competência em gerenciamento de estado complexo, arquitetura modular e implementação de sistemas multi-usuário.

**Objetivo:** Desenvolver uma aplicação frontend sofisticada que simule um sistema TODO List multi-usuário, demonstrando expertise em arquitetura de componentes, gerenciamento de estado avançado e boas práticas de desenvolvimento.

## Especificações Técnicas

### Requisitos Funcionais

#### Sistema de Autenticação Simulada

1. **Interface de Login**: Tela de autenticação com validação de campos não vazios
2. **Autenticação Mock**: Qualquer combinação válida de usuário/senha redireciona para a aplicação
3. **Persistência de Sessão**: Manter dados do usuário logado no estado global da aplicação

#### Gerenciamento Avançado de Listas

1. **Múltiplas Listas de Tarefas**: Usuário pode criar listas categorizadas (ex: "Trabalho", "Estudos", "Pessoal")
2. **CRUD Completo de Listas**:
   * Criar novas listas com nomes personalizados
   * Renomear listas existentes com validação
   * Excluir listas com confirmação e verificação de dependências
3. **Navegação entre Listas**: Interface intuitiva para alternar entre diferentes listas

#### Sistema Completo de Tarefas

1. **Gerenciamento por Lista**: Cada lista mantém suas próprias tarefas independentemente
2. **CRUD de Tarefas**: Adicionar, editar, remover e marcar tarefas como concluídas dentro de cada lista
3. **Validações Avançadas**: Prevenção de duplicatas, validação de campos obrigatórios

#### Persistência e Navegação

1. **Estado Persistente**: Todo o estado (usuário, listas, tarefas) gerenciado pelo Pinia e persistido
2. **Roteamento**: Vue Router para separar autenticação da aplicação principal
3. **Guards de Rota**: Proteção de rotas para usuários não autenticados


### Stack Tecnológica Obrigatória

* **Framework**: Vue 3 (Composition API)
* **Roteamento**: Vue Router 4
* **Gerenciamento de Estado**: Pinia
* **UI Framework**: Material Design (Vuetify ou biblioteca equivalente)
* **Testes**: Vitest para testes unitários abrangentes
* **TypeScript**: Fortemente recomendado para tipagem robusta

# BACKEND

## Especificações Técnicas

### Requisitos Funcionais

#### Sistema de Autenticação Segura

1. **Registro de Usuários**:
   * Endpoint `POST /auth/register` para cadastro com nome, email e senha
   * Implementação de hash seguro de senhas utilizando bcrypt
   * Validação de unicidade de email
2. **Autenticação JWT**:
   * Endpoint `POST /auth/login` para autenticação e geração de token JWT
   * Implementação de refresh token para segurança aprimorada

#### Gerenciamento de Tarefas com Segurança

1. **CRUD Completo de Tarefas**:
   * `POST /tasks`: Criar tarefa associada ao usuário autenticado
   * `GET /tasks`: Listar exclusivamente tarefas do usuário logado
   * `GET /tasks/{id}`: Buscar tarefa específica com validação de propriedade
   * `PUT /tasks/{id}`: Atualizar tarefa com controle de acesso
   * `DELETE /tasks/{id}`: Remover tarefa com validação de proprietário
2. **Autorização Robusta**: Todas as rotas protegidas por JWT com validação de propriedade dos recursos

### Requisitos Não Funcionais

#### Arquitetura e Design Patterns

1. **Princípios SOLID**: Implementação rigorosa dos cinco princípios em todas as camadas
2. **Arquitetura em Camadas**: Estrutura bem definida (Controller → Service → Repository → Domain)
3. **Injeção de Dependência**: Utilização adequada do Spring Framework para IoC
4. **Exception Handling**: Sistema robusto de tratamento centralizado de exceções

#### Qualidade e Testabilidade

1. **Testes Unitários**: Cobertura completa da camada de serviço com cenários de sucesso e falha
2. **Testes de Integração**: Validação end-to-end dos endpoints com Spring Test
3. **Mocks e Stubs**: Utilização adequada de Mockito para isolamento de dependências

### Stack Tecnológica Obrigatória

* **Linguagem**: Java 17+
* **Framework**: Spring Boot, Spring Security, Spring Validation
* **Persistência**: Spring Data JPA com Hibernate
* **Banco de Dados**: PostgreSQL
* **Segurança**: JWT, BCrypt
* **Testes**: JUnit 5, Mockito, Spring Boot Test

## Critérios de Avaliação

* **Aplicação de SOLID**: Demonstração clara e justificada dos princípios SOLID (critério principal)
* **Qualidade Arquitetural**: Design limpo, modular com separação clara de responsabilidades
* **Cobertura de Testes**: Suite robusta e significativa de testes unitários e de integração
* **Implementação de Segurança**: Autenticação e autorização corretamente implementadas
* **Domínio da Stack**: Utilização avançada e adequada do ecossistema Spring
* **Domínio da Stack**: Utilização avançada das ferramentas do ecossistema Vue.js
* **Modelagem de Dados**: Relacionamento bem definido entre entidades User e Task
* **Documentação Técnica**: README detalhado com justificativas arquiteturais

## Expectativa de Entrega

* **Prazo**: Até 3 dias corridos a partir do recebimento.
* **Formato**: Repositório Git com código-fonte completo e documentação detalhada.

### Estrutura Obrigatória do `README.md`

1. **Visão Geral da Arquitetura**: Descrição detalhada da estrutura e decisões arquiteturais
2. **Stack Tecnológica**: Lista completa com justificativas para cada escolha
3. **Como Rodar Localmente**: Instruções passo a passo para setup e execução
4. **Como Rodar os Testes**: Comandos para executar suite completa de testes
5. **Estrutura de Pastas Detalhada**: Mapeamento completo da organização modular do código
6. **Decisões Técnicas Aprofundadas**: Justificativas detalhadas sobre escolhas arquiteturais, padrões e bibliotecas
7. **Melhorias e Roadmap**: Propostas técnicas para evolução e escalabilidade da aplicação

---

**Boa sorte! A JTech espera uma solução que demonstre maturidade em desenvolvimento frontend e visão arquitetural.**
