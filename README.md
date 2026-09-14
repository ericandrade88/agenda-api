# Agenda API — Agência de Viagem

API RESTful desenvolvida em **Java 17 + Spring Boot 3** para uma agência de viagens,
conforme o desafio proposto. Não há integração com banco de dados nem segurança —
os dados ficam em memória, dentro da camada de serviço, como pedido no enunciado.

## Como executar

Pré-requisitos: JDK 17+ e Maven (ou use o `./mvnw` se preferir gerar o wrapper).

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

## Estrutura do projeto

```
src/main/java/com/agenciaviagem/agendaapi/
├── AgendaApiApplication.java
├── model/
│   ├── Destino.java
│   └── Reserva.java
├── dto/
│   ├── DestinoRequest.java
│   ├── AvaliacaoRequest.java
│   └── ReservaRequest.java
├── service/
│   ├── DestinoService.java      -> regra de negócio de destinos
│   └── ReservaService.java      -> regra de negócio de reservas
├── controller/
│   ├── DestinoController.java   -> endpoints /api/destinos
│   └── ReservaController.java   -> endpoints /api/destinos/{id}/reservas
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

A separação entre `controller` (recebe a requisição HTTP) e `service` (lógica de
negócio) atende ao critério de avaliação sobre a camada de serviço.

## Endpoints

### 1. Cadastrar destino
```
POST /api/destinos
Content-Type: application/json

{
  "nome": "Rio de Janeiro",
  "localizacao": "Rio de Janeiro, Brasil",
  "descricao": "Cidade maravilhosa, praias e Cristo Redentor",
  "precoPacote": 1500.00
}
```
Resposta: `201 Created` com o destino criado (id gerado automaticamente).

### 2. Listar todos os destinos
```
GET /api/destinos
```
Resposta: `200 OK` com a lista de destinos.

### 3. Pesquisar destinos por nome ou localização
```
GET /api/destinos/pesquisa?nome=rio
GET /api/destinos/pesquisa?localizacao=brasil
GET /api/destinos/pesquisa?nome=rio&localizacao=brasil
```
Busca parcial e sem distinção de maiúsculas/minúsculas. Os dois parâmetros são
opcionais.

### 4. Ver detalhes de um destino
```
GET /api/destinos/{id}
```
Resposta: `200 OK` com o destino, ou `404 Not Found` se não existir.

### 5. Avaliar um destino (nota de 1 a 10)
```
PATCH /api/destinos/{id}/avaliacoes
Content-Type: application/json

{
  "nota": 9
}
```
O serviço recalcula a média (`notaMedia`) com base na nota já existente e na
quantidade de avaliações recebidas até então.

### 6. Excluir um destino
```
DELETE /api/destinos/{id}
```
Resposta: `204 No Content`, ou `404 Not Found` se o destino não existir.

### Extra: atualizar dados de um destino
```
PUT /api/destinos/{id}
```
Mesmo corpo do cadastro — sobrescreve os dados cadastrais do destino.

### Extra: reservar um pacote de viagem
```
POST /api/destinos/{id}/reservas
Content-Type: application/json

{
  "nomeCliente": "Maria Souza",
  "numeroPessoas": 2
}
```
Resposta: `201 Created` com a reserva criada.

```
GET /api/destinos/{id}/reservas
```
Lista as reservas feitas para aquele destino.

## Tratamento de erros

Erros de validação (`400 Bad Request`) e destino não encontrado
(`404 Not Found`) retornam um JSON padronizado, por exemplo:

```json
{
  "timestamp": "2026-09-13T10:00:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Destino nao encontrado com id: 5"
}
```

## Testando rapidamente com curl

```bash
# cadastrar
curl -X POST http://localhost:8080/api/destinos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Gramado","localizacao":"Rio Grande do Sul, Brasil","descricao":"Serra gaucha","precoPacote":900.00}'

# listar
curl http://localhost:8080/api/destinos

# avaliar (assumindo id=1)
curl -X PATCH http://localhost:8080/api/destinos/1/avaliacoes \
  -H "Content-Type: application/json" -d '{"nota":8}'

# excluir
curl -X DELETE http://localhost:8080/api/destinos/1
```
