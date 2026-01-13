# ClinSys - Medical Appointment Management System

Sistema de gerenciamento de atendimentos para a clínica Vida Plena, integrando Backend (Spring Boot) e Frontend (React).

## 🚀 Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3**
- **PostgreSQL**
- **Spring Security + JWT**
- **Swagger / OpenAPI**

### Frontend
- **React + Vite**
- **Tailwind CSS**
- **Nginx (Produção Docker)**

### Infraestrutura
- **Docker & Docker Compose**

## 🛠️ Configuração e Execução

### Pré-requisitos
- Docker e Docker Compose

### Executando a Aplicação (Recomendado)

Para iniciar todos os serviços (Backend, Frontend e Banco de Dados) via Docker:

```bash
docker-compose up -d --build
```

Após a inicialização:

- **Frontend (Painel)**: [http://localhost:3000](http://localhost:3000)
- **Backend API**: `http://localhost:8080/api`
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Documentação da API (Swagger)

Acesse a documentação interativa em:
👉 **http://localhost:8080/swagger-ui.html**

A especificação OpenAPI em formato JSON pode ser acessada em:
👉 **http://localhost:8080/v3/api-docs**

## 🔐 Autenticação e Perfis

O sistema utiliza JWT. O primeiro passo é criar um usuário (ou usar um existente) e fazer login.

### Perfis de Acesso

| Perfil | Permissões |
|--------|------------|
| **ADMIN** | Acesso total (Criar, Listar, Editar, Remover, Alterar Status). |
| **RECEPTIONIST** | Criar e Listar. Não pode remover ou editar finalizados. |
| **DOCTOR** | Listar e Atualizar Status. Não pode criar ou remover. |

### Fluxo de Uso (Frontend)

1. Acesse [http://localhost:3000](http://localhost:3000).
2. Clique em "Criar conta" para registrar um novo usuário (ex: `admin` / role `ADMIN`).
3. Faça login com as credenciais criadas.
4. No Dashboard, você poderá:
   - Criar novos agendamentos.
   - Visualizar a lista de agendamentos.
   - Alterar o status (SCHEDULED → IN_PROGRESS → COMPLETED).
   - Excluir agendamentos (Apenas ADMIN).

## 🧪 Testes

Para executar os testes unitários e de integração do Backend (requer Java/Maven local):

```bash
./mvnw test
```
