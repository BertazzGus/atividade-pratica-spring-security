# ToDoList com Spring Security

API de tarefas (cadastrar, listar, buscar por ID e excluir) protegida com **autenticação** (quem é o usuário) e **autorização** (o que ele pode fazer), usando Spring Security.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Security (HTTP Basic, `SecurityFilterChain`, `PasswordEncoder` BCrypt)
- Spring Data JPA + MySQL

## Pré-requisitos

- JDK 21
- MySQL em execução em `localhost:3306`, com o banco `todolist` e o usuário `aluno` (senha `aluno`), conforme `application.properties`

## Como executar

```bash
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`.

## Testes

```bash
./mvnw test
```

Os testes de autorização estão em `TarefaControllerSecurityTests`. Como usam `@SpringBootTest`, o MySQL precisa estar em execução (o mesmo vale para o teste `contextLoads`).

## Implementação

- **Dependência:** `spring-boot-starter-security` (e `spring-boot-starter-security-test` para testes).
- **Configuração:** `br.edu.ifpr.todolist.config.SecurityConfig`
  - `SecurityFilterChain` define as regras de acesso e habilita HTTP Basic.
  - `PasswordEncoder` com `BCryptPasswordEncoder`. As senhas são armazenadas codificadas.
  - `UserDetailsService` com `InMemoryUserDetailsManager`, com dois usuários em memória.
- Sessão `STATELESS` e CSRF desabilitado, por se tratar de uma API que autentica a cada requisição via cabeçalho `Authorization`.

## Usuários em memória

| Usuário   | Senha      | Perfil |
|-----------|------------|--------|
| `usuario` | `usuario123` | USER   |
| `admin`   | `admin123`   | ADMIN  |

Não há cadastro de usuários, persistência de usuários no banco nem JWT nesta versão.

## Permissões

| Operação              | Endpoint                 | Sem autenticação | USER | ADMIN |
|-----------------------|--------------------------|:----------------:|:----:|:-----:|
| Listar tarefas        | `GET /tarefas`           | Não permitido (401) | Permitido | Permitido |
| Buscar tarefa por ID  | `GET /tarefas/{id}`      | Não permitido (401) | Permitido | Permitido |
| Cadastrar tarefa      | `POST /tarefas`          | Não permitido (401) | Permitido | Permitido |
| Excluir tarefa        | `DELETE /tarefas/{id}`   | Não permitido (401) | Não permitido (403) | Permitido |

## Exemplos de uso (curl)

Listar tarefas (USER ou ADMIN):

```bash
curl -u usuario:usuario123 http://localhost:8080/tarefas
```

Cadastrar tarefa (USER ou ADMIN):

```bash
curl -u usuario:usuario123 -H "Content-Type: application/json" \
     -d '{"titulo":"Estudar Spring Security","descricao":"Atividade prática","prazo":"2026-10-30"}' \
     http://localhost:8080/tarefas
```

Buscar tarefa por ID (USER ou ADMIN):

```bash
curl -u usuario:usuario123 http://localhost:8080/tarefas/1
```

Excluir tarefa (somente ADMIN):

```bash
# Retorna 403 para o USER
curl -u usuario:usuario123 -X DELETE http://localhost:8080/tarefas/1

# Retorna 204 para o ADMIN
curl -u admin:admin123 -X DELETE http://localhost:8080/tarefas/1
```

Sem autenticação, qualquer uma das chamadas retorna `401 Unauthorized`.
