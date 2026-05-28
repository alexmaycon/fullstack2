![Jtech Logo](http://www.jtech.com.br/wp-content/uploads/2015/06/logo.png)

# jtech-tasklist

## What is

API REST do desafio fullstack **JTech Tasklist**: um sistema multi-usuário de listas de tarefas (TODO List) construído em **Java 21 + Spring Boot 3.5**, com autenticação JWT, persistência em PostgreSQL via Spring Data JPA + Liquibase e arquitetura **Hexagonal (Ports & Adapters)** aplicando os princípios SOLID.

Funcionalidades expostas:

- Cadastro e autenticação de usuários (`/auth/register`, `/auth/login`, `/auth/refresh`).
- CRUD de listas de tarefas (`/task-lists`), escopadas pelo usuário autenticado.
- CRUD de tarefas (`/tasks`) com paginação, filtro por lista e marcação de conclusão.
- Documentação interativa via **Swagger UI / OpenAPI 3**.

## Composite by

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.5 (Web, Validation, Security, Data JPA, Actuator) |
| Persistência | Spring Data JPA + Hibernate |
| Migrações | Liquibase (SQL, padrão `ALEX:sequencial`) |
| Banco | PostgreSQL 16 (H2 em testes, modo PostgreSQL) |
| Segurança | Spring Security + JWT (`jjwt 0.12.6`) + BCrypt (strength 12) |
| Mapeamento | ModelMapper 3.2.1 |
| Documentação | springdoc-openapi 2.8.13 (Swagger UI) |
| Testes | JUnit 5, Mockito, AssertJ, Spring Security Test |
| Cobertura | Jacoco 0.8.12 |
| Build | Gradle 8.14 |

## Services

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/v1/auth/register` | Cadastra novo usuário | Pública |
| POST | `/api/v1/auth/login` | Autentica e retorna `accessToken` + `refreshToken` | Pública |
| POST | `/api/v1/auth/refresh` | Renova o `accessToken` a partir do `refreshToken` | Pública |
| GET | `/api/v1/task-lists` | Lista as listas do usuário autenticado | JWT |
| POST | `/api/v1/task-lists` | Cria nova lista | JWT |
| PUT | `/api/v1/task-lists/{id}` | Renomeia lista | JWT |
| DELETE | `/api/v1/task-lists/{id}` | Remove lista | JWT |
| GET | `/api/v1/tasks?taskListId=&page=&size=` | Lista tarefas paginadas | JWT |
| GET | `/api/v1/tasks/{id}` | Busca tarefa por id | JWT |
| POST | `/api/v1/tasks` | Cria tarefa | JWT |
| PUT | `/api/v1/tasks/{id}` | Atualiza tarefa | JWT |
| PATCH | `/api/v1/tasks/{id}/toggle` | Alterna status concluída/pendente | JWT |
| DELETE | `/api/v1/tasks/{id}` | Remove tarefa | JWT |

Documentação interativa: <http://localhost:8080/swagger-ui/index.html>

## Helper

Estrutura adotada segue a **Arquitetura Hexagonal**:

```
br.com.jtech.tasklist
├── StartTasklist.java                  Bootstrap Spring Boot
├── adapters/
│   ├── input/
│   │   ├── controllers/                REST Controllers (lado entrada)
│   │   └── protocols/                  DTOs de Request/Response
│   └── output/
│       └── repositories/               Spring Data JPA + Entities (lado saída)
├── application/
│   ├── core/
│   │   ├── domains/                    Modelos de domínio puros
│   │   └── usecases/                   Casos de uso (regras de negócio)
│   └── ports/
│       ├── input/                      Interfaces que o domínio expõe
│       └── output/                     Interfaces que o domínio consome
└── config/
    ├── infra/
    │   ├── exceptions/                 Handler global e exceções de domínio
    │   ├── swagger/                    Configuração OpenAPI
    │   └── utils/                      JWT, filtros de segurança, BCrypt
    └── usecases/                       Wiring dos casos de uso
```

Princípios aplicados:

- **SRP**: cada use case implementa uma única ação de negócio.
- **DIP**: controllers e adapters dependem apenas de **ports** (interfaces).
- **OCP/LSP**: novos adapters (ex.: troca de banco) sem alterar o domínio.
- **ISP**: ports segregadas (`I*UseCase`, `I*Adapter`) por intenção.

## How to use

### Pré-requisitos

- Java 21+
- Docker (para subir o PostgreSQL local)

### 1. Subir o banco

```sh
cd composer
docker compose up -d
```

Provisiona o serviço `tasklist_postgres` em `localhost:5432` (db `tasklist_db`, usuário `postgres`, senha `postgres`).

### 2. Rodar a aplicação

Windows:

```sh
.\gradlew.bat bootRun
```

Linux/macOS:

```sh
./gradlew bootRun
```

A API sobe em <http://localhost:8080>. O Liquibase executa as migrações automaticamente.

### 3. Variáveis úteis (opcionais)

| Variável | Default |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tasklist_db` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` |
| `JWT_SECRET` | definido em `application.yml` |
| `JWT_EXPIRATION` | tempo do access token em ms |

## Sample

### Cadastro

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Alex",
  "email": "alex@jtech.com.br",
  "password": "123456"
}
```

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "alex@jtech.com.br",
  "password": "123456"
}
```

Resposta:

```json
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi...",
  "tokenType": "Bearer"
}
```

### Criar lista

```http
POST /api/v1/task-lists
Authorization: Bearer <accessToken>
Content-Type: application/json

{ "name": "Trabalho" }
```

### Criar tarefa

```http
POST /api/v1/tasks
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "taskListId": "f3a1...",
  "title": "Revisar PR",
  "description": "Code review do módulo de auth"
}
```

## How to run

### Build completo

```sh
./gradlew clean build
```

### Apenas testes unitários e de integração

```sh
./gradlew test
```

### Relatório de cobertura (Jacoco)

```sh
./gradlew jacocoTestReport
```

Relatório HTML em `build/reports/jacoco/test/html/index.html`. Cobertura atual: **≥ 80% de instruções**.

### Empacotar `.jar`

```sh
./gradlew bootJar
java -jar build/libs/jtech-tasklist-*.jar
```

## Points to improve

- **Cache** de listas/tarefas por usuário (Redis/Caffeine) para reduzir hits ao banco.
- **Rate limiting** nos endpoints de autenticação (Bucket4j) para mitigar brute force.
- **Auditoria com Hibernate Envers** nas tabelas `task_list` e `task`.
- **Observabilidade**: tracing distribuído (OpenTelemetry) e métricas Prometheus via Actuator.
- **Logs centralizados** em Graylog/ELK com correlação de request.
- **Soft delete** padronizado em todas as entidades de negócio.
- **Pipeline CI/CD** (GitHub Actions) com publish no Nexus e deploy em container.
- **Internacionalização (i18n)** das mensagens de erro retornadas pela API.
- **Refresh token rotativo** persistido em base, com revogação.
