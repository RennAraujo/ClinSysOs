# ClinSys - Medical Appointment Management System

Sistema de gerenciamento de atendimentos para a clínica Vida Plena.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Spring Security + JWT**
- **Swagger / OpenAPI**

## 🛠️ Configuração e Execução

### Pré-requisitos
- Java 21+
- Maven
- Docker e Docker Compose

### Configuração do Ambiente

1. Copie o arquivo de exemplo de variáveis de ambiente:
   ```bash
   cp .env.example .env
   ```
2. Ajuste as variáveis no arquivo `.env` se necessário.

### Executando com Docker (Banco de Dados)

Para iniciar o banco de dados PostgreSQL:

```bash
docker-compose up -d
```

### Executando a Aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## 📚 Documentação da API

A documentação interativa (Swagger UI) pode ser acessada em:
`http://localhost:8080/swagger-ui.html` (após iniciar a aplicação)
