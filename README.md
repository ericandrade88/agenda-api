# Agenda API — Agência de Viagem

API RESTful em **Java 17 + Spring Boot 3** para uma agência de viagens.

* **Desafio 1** — API com camada de serviço e dados em memória.
* **Desafio 2** (esta versão) — mesma API, agora com **PostgreSQL via Spring Data JPA** e
  **Spring Security** (autenticação com usuários do banco + autorização por perfil).

Os endpoints e o formato do JSON do desafio 1 foram mantidos.

## Como executar

Pré-requisitos: JDK 17+, Maven e Docker (ou um PostgreSQL local).

```bash
docker compose up -d          # sobe o PostgreSQL (banco: agenda_api)
mvn spring-boot:run           # sobe a API em http://localhost:8080
```

Configuração em `src/main/resources/application.yml`. Credenciais do banco podem ser trocadas
pelas variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`
(padrão: `jdbc:postgresql://localhost:5432/agenda_api`, `postgres` / `postgres`).
As tabelas são criadas automaticamente (`ddl-auto: update`).

## Banco de dados

| Tabela | Descrição |
|---|---|
| `destinos` | nome, localizacao, descricao, preco_pacote, nota_media, quantidade_avaliacoes |
| `reservas` | nome_cliente, numero_pessoas, data_reserva, `destino_id` (FK → destinos) |
| `usuarios` | nome, email (único), senha (BCrypt), ativo |
| `perfis` | ADMIN, CLIENTE, PARCEIRO |
| `usuarios_perfis` | relação N:N entre usuários e perfis |

Excluir um destino remove também as reservas dele (cascade).

## Segurança

Autenticação **HTTP Basic**: o `UsuarioDetailsService` busca o usuário pelo e-mail no banco e
compara a senha com o hash BCrypt. Cada perfil vira a authority `ROLE_<perfil>`.

Usuários criados na primeira execução (`DataInitializer`):

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | admin@agencia.com | admin123 |
| CLIENTE | cliente@agencia.com | cliente123 |
| PARCEIRO | parceiro@agencia.com | parceiro123 |

### Permissões por perfil

| Endpoint | ADMIN | CLIENTE | PARCEIRO | Anônimo |
|---|:-:|:-:|:-:|:-:|
| `POST /api/auth/registro` (cria CLIENTE) | ✅ | ✅ | ✅ | ✅ |
| `GET /api/auth/me` | ✅ | ✅ | ✅ | ❌ |
| `GET /api/destinos`, `/pesquisa`, `/{id}` | ✅ | ✅ | ✅ | ❌ |
| `POST /api/destinos` | ✅ | ❌ | ❌ | ❌ |
| `PUT /api/destinos/{id}` | ✅ | ❌ | ❌ | ❌ |
| `DELETE /api/destinos/{id}` | ✅ | ❌ | ❌ | ❌ |
| `PATCH /api/destinos/{id}/avaliacoes` | ✅ | ✅ | ❌ | ❌ |
| `POST /api/destinos/{id}/reservas` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/destinos/{id}/reservas` | ✅ | ❌ | ❌ | ❌ |
| `/api/usuarios/**` (listar, trocar perfis, desativar) | ✅ | ❌ | ❌ | ❌ |

Sem credenciais ou com senha errada → `401 Unauthorized`. Perfil sem permissão → `403 Forbidden`.

## Estrutura do projeto

```
src/main/java/com/agenciaviagem/agendaapi/
├── AgendaApiApplication.java
├── config/
│   ├── SecurityConfig.java        -> regras de acesso por perfil + BCrypt
│   └── DataInitializer.java       -> perfis e usuários iniciais
├── model/                         -> entidades JPA: Destino, Reserva, Usuario, Perfil
├── repository/                    -> Spring Data JPA: Destino, Reserva, Usuario, Perfil
├── security/
│   └── UsuarioDetailsService.java -> autenticação via banco
├── dto/                           -> DestinoRequest, AvaliacaoRequest, ReservaRequest, RegistroRequest, PerfisRequest
├── service/                       -> DestinoService, ReservaService (regra de negócio)
├── controller/                    -> Destino, Reserva, Auth, Usuario
└── exception/                     -> ResourceNotFoundException, GlobalExceptionHandler
```

## Endpoints de destinos (iguais ao desafio 1)

```
POST   /api/destinos                      cadastrar            (ADMIN)
GET    /api/destinos                      listar               (qualquer perfil)
GET    /api/destinos/pesquisa?nome=&localizacao=   pesquisar   (qualquer perfil)
GET    /api/destinos/{id}                 detalhar             (qualquer perfil)
PUT    /api/destinos/{id}                 atualizar            (ADMIN)
PATCH  /api/destinos/{id}/avaliacoes      avaliar, nota 1-10   (CLIENTE, ADMIN)
DELETE /api/destinos/{id}                 excluir              (ADMIN)
POST   /api/destinos/{id}/reservas        reservar             (CLIENTE, ADMIN)
GET    /api/destinos/{id}/reservas        listar reservas      (ADMIN)
```

## Testando com curl

```bash
# cadastrar destino (ADMIN)
curl -u admin@agencia.com:admin123 -X POST http://localhost:8080/api/destinos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Gramado","localizacao":"Rio Grande do Sul, Brasil","descricao":"Serra gaucha","precoPacote":900.00}'

# listar (qualquer perfil)
curl -u parceiro@agencia.com:parceiro123 http://localhost:8080/api/destinos

# avaliar (CLIENTE)
curl -u cliente@agencia.com:cliente123 -X PATCH http://localhost:8080/api/destinos/1/avaliacoes \
  -H "Content-Type: application/json" -d '{"nota":8}'

# reservar (CLIENTE)
curl -u cliente@agencia.com:cliente123 -X POST http://localhost:8080/api/destinos/1/reservas \
  -H "Content-Type: application/json" -d '{"nomeCliente":"Maria Souza","numeroPessoas":2}'

# parceiro tentando excluir -> 403
curl -u parceiro@agencia.com:parceiro123 -X DELETE http://localhost:8080/api/destinos/1

# cadastrar novo cliente
curl -X POST http://localhost:8080/api/auth/registro -H "Content-Type: application/json" \
  -d '{"nome":"Joao","email":"joao@teste.com","senha":"123456"}'
```

## Testes

```bash
mvn test
```

Usam H2 em memória (perfil `test`), sem precisar do PostgreSQL:
* `DestinoServiceTest` — regras do serviço com JPA real.
* `DestinoControllerIntegrationTest` — fluxo completo com autenticação.
* `SegurancaIntegrationTest` — 401/403/200/201 por perfil.

## Tratamento de erros

Validação (`400`), não encontrado (`404`), não autenticado (`401`) e sem permissão (`403`).
Respostas de erro da aplicação seguem o JSON padronizado `{timestamp, status, erro, mensagem}`.
