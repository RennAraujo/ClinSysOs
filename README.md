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
- Docker e Docker Compose

### Executando a Aplicação (Recomendado)

Para iniciar a aplicação e o banco de dados via Docker:

```bash
docker-compose up -d --build
```

A API estará disponível em `http://localhost:8080`.

### Documentação da API (Swagger)

Acesse a documentação interativa em:
👉 **http://localhost:8080/swagger-ui.html**

A especificação OpenAPI em formato JSON pode ser acessada em:
👉 **http://localhost:8080/v3/api-docs**

> **Nota:** O projeto utiliza SpringDoc OpenApi compatível com Spring Boot 3.3.x para garantir a geração correta da documentação.

## 🔐 Autenticação e Perfis

O sistema utiliza JWT. Crie um usuário e faça login para obter o token.

| Perfil | Permissões |
|--------|------------|
| **ADMIN** | Acesso total (Criar, Listar, Editar, Remover). |
| **RECEPTIONIST** | Criar e Listar. Não pode remover ou editar finalizados. |
| **DOCTOR** | Listar e Atualizar Status. Não pode criar ou remover. |

### Exemplo de Fluxo

1. **Registrar Usuário**:
   `POST /api/auth/register`
   ```json
   {
     "username": "admin",
     "password": "123",
     "role": "ADMIN"
   }
   ```

2. **Login**:
   `POST /api/auth/login`
   - Copie o `token` da resposta.

3. **Usar a API**:
   - No Swagger, clique em **Authorize** e insira: `Bearer <SEU_TOKEN>`.

## 🧪 Testes

Para executar os testes unitários (requer Java/Maven local):

```bash
./mvnw test
```
