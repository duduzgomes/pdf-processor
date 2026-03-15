# PDF Processor

Sistema assíncrono de processamento de PDFs com OCR para validação de documentos.

## Arquitetura

```
Client → pdf-api-service → MinIO (storage)
                         → PostgreSQL (jobs)
                         → RabbitMQ (queue)
                                  ↓
                      pdf-worker-service
                         → PDFBox + Tess4J (OCR)
                         → Webhook Dispatcher
```

## Serviços

| Serviço            | Responsabilidade                       | Porta        |
| ------------------ | -------------------------------------- | ------------ |
| pdf-api-service    | Recebe PDFs, cria jobs, expõe status   | 8080         |
| pdf-worker-service | Processa PDFs via OCR, dispara webhook | 8081         |
| PostgreSQL         | Persistência dos jobs                  | 5432         |
| RabbitMQ           | Fila de processamento + DLQ            | 5672 / 15672 |
| MinIO              | Object storage dos PDFs                | 9000 / 9001  |

## Como rodar

### 1. Suba a infraestrutura

```bash
docker-compose up -d
```

### 2. Rode a API

```bash
cd pdf-api-service
./mvnw spring-boot:run
```

### 3. Rode o Worker

```bash
cd pdf-worker-service
./mvnw spring-boot:run
```

## Endpoints

### POST /api/documents

Envia um PDF para processamento.

**Form-data:**

- `file` — arquivo PDF
- `expectedName` — nome a ser buscado no documento
- `webhookUrl` — URL que receberá a notificação quando o processamento terminar

**Response:** `202 Accepted`

```json
{
  "jobId": "uuid",
  "status": "PENDING",
  "message": "Documento recebido. Você será notificado via webhook."
}
```

### GET /api/jobs/{id}

Consulta o status de um job.

## Swagger UI

Disponível em `http://localhost:8080/swagger-ui.html` após subir a API.

## Tecnologias

- Java 21
- Spring Boot 3.2
- PostgreSQL + Flyway
- RabbitMQ (AMQP)
- MinIO (S3-compatible)
- PDFBox + Tess4J (OCR)
- Docker

```

---

## 4. Verifique a estrutura final

Antes de commitar, confirme que está assim:
```

pdf-processor/
├── pdf-api-service/
│ ├── src/
│ ├── pom.xml
│ └── mvnw
├── pdf-worker-service/
│ ├── src/
│ ├── pom.xml
│ └── mvnw
├── docker-compose.yml
├── .gitignore
└── README.md
