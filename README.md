# Password Vault

[![CI](https://github.com/ybueno16/SecureApplication/actions/workflows/ci.yml/badge.svg)](https://github.com/ybueno16/SecureApplication/actions/workflows/ci.yml)
[![Coverage](https://raw.githubusercontent.com/ybueno16/SecureApplication/master/.github/badges/jacoco.svg)](https://github.com/ybueno16/SecureApplication/actions/workflows/ci.yml)

A secure credential management vault built with **Java 21**, **Spring Boot 3.4**, following **DDD** and **Object Calisthenics** principles.

---

## Architecture (simple, readable)

### High-level request flow

```mermaid
flowchart LR
  client[Client] --> rl["Rate Limiting Filter"]
  rl --> jwt["JWT Auth Filter"]
  jwt --> ctrl["HTTP Controllers"]
  ctrl --> uc["Use Cases (Application)"]
  uc --> dom["Domain Services (Domain)"]
  uc --> repo["Repositories (Infrastructure)"]
  repo --> db[("PostgreSQL")]
  dom --> crypto["Encryption Service"]
```

### Layer dependency rules (DDD)

- `interfaces` depends on `application`
- `application` depends on `domain`
- `infrastructure` depends on `domain`
- `infrastructure` implements domain interfaces (Dependency Inversion)

```mermaid
flowchart TD
  interfaces["interfaces (http)"] --> application["application"]
  application --> domain["domain"]
  infrastructure["infrastructure"] --> domain
  infrastructure -. "implements" .-> domain
```

---

## Encryption / Decryption Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant API as API
    participant Crypto as CryptoService
    participant DB as PostgreSQL

    Note over C,DB: === Store Credential ===
    C->>API: POST /credentials + X-Master-Password
    API->>DB: Fetch user KDF salt
    API->>Crypto: deriveKey(masterPassword, salt) via PBKDF2 (600k iterations)
    Crypto-->>API: 256-bit AES key (ephemeral)
    API->>Crypto: encrypt(username, key) → AES-256-GCM + random 96-bit IV
    API->>Crypto: encrypt(password, key) → AES-256-GCM + random 96-bit IV
    API->>Crypto: encrypt(notes, key) → AES-256-GCM + random 96-bit IV
    API->>DB: Store IV||ciphertext per field
    Note over API: Key wiped from memory

    Note over C,DB: === Reveal Credential ===
    C->>API: GET /credentials/{id}/reveal + X-Master-Password
    API->>DB: Fetch user salt + encrypted fields
    API->>Crypto: deriveKey(masterPassword, salt)
    API->>Crypto: decrypt(encryptedField, key) for each field
    Crypto-->>API: plaintext
    API-->>C: { username, password, notes }
    Note over API: Key wiped from memory
```

---

## Object Calisthenics Compliance Checklist

| # | Rule | Status | Notes |
|---|------|--------|-------|
| 1 | One level of indentation | ✅ | Stream pipelines, guard clauses, early returns |
| 2 | No ELSE keyword | ✅ | Guard clauses + `Optional` throughout domain/application |
| 3 | Wrap all primitives and strings | ✅ | 15+ Value Objects (UserId, Email, PasswordHash, etc.) |
| 4 | First-class collections | ✅ | `Tags` class with domain behavior |
| 5 | One dot per line | ✅ | Delegation methods on entities (e.g., `credential.encryptedPassword()`) |
| 6 | Don't abbreviate | ✅ | Full names: `CredentialRepository`, `FailedLoginAttempts`, etc. |
| 7 | Keep entities small | ✅ | Domain ≤100 lines, Application ≤150 lines |
| 8 | Max 2 instance variables | ✅ | Composed VOs; documented exceptions for 3-field VOs |
| 9 | No getters/setters | ✅ | Behavior methods + `to*()` accessors for persistence (OC-9 documented) |

**Relaxations** (infrastructure/interfaces only, marked with `// OC-relaxed`):
- Spring Security DSL method chaining
- Controller constructors with multiple use case dependencies
- JDBC RowMapper inner classes

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Build | Gradle (Kotlin DSL) |
| Framework | Spring Boot 3.4 + Spring Security 6 |
| Database | PostgreSQL 16 + NamedParameterJdbcTemplate (zero JPA) |
| Migrations | Flyway |
| Validation | Jakarta Validation 3 |
| Docs | Springdoc OpenAPI 2 |
| Auth | JWT RS256 (JJWT) + Argon2id |
| Encryption | AES-256-GCM + PBKDF2-HMAC-SHA256 |
| Rate Limiting | Bucket4j + Caffeine |
| Testing | JUnit 5 + Mockito + Testcontainers |

---

## Setup & Run

### Prerequisites
- Java 21+
- Docker & Docker Compose

### With Docker Compose
```bash
docker compose up -d
```
App available at `http://localhost:8080`  
Swagger UI at `http://localhost:8080/swagger-ui.html`

### Local Development
```bash
# Start PostgreSQL
docker compose up -d postgres

# Run the app
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Run Tests
```bash
# Unit tests only
./gradlew test --tests "com.vault.domain.*" --tests "com.vault.application.*"

# Integration tests (embedded PostgreSQL — no Docker required)
./gradlew test --tests "com.vault.integration.*"

# All tests with coverage report
./gradlew test jacocoTestReport
# Report: build/reports/jacoco/test/html/index.html
```

---

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/auth/register` | No | Register a new user |
| `POST` | `/api/v1/auth/login` | No | Login → access token + refresh token |
| `POST` | `/api/v1/auth/refresh` | No | Refresh token pair |
| `POST` | `/api/v1/auth/logout` | Bearer | Revoke all refresh tokens |
| `POST` | `/api/v1/credentials` | Bearer + `X-Master-Password` | Create encrypted credential |
| `GET` | `/api/v1/credentials` | Bearer | List credentials (cursor pagination) |
| `PUT` | `/api/v1/credentials/{id}` | Bearer + `X-Master-Password` | Update credential |
| `DELETE` | `/api/v1/credentials/{id}` | Bearer | Delete credential |
| `GET` | `/api/v1/credentials/{id}/reveal` | Bearer + `X-Master-Password` | Decrypt & reveal credential |
| `GET` | `/api/v1/generator` | No | Generate random secure password |

### Query Parameters for `/api/v1/generator`

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `length` | int | 24 | Password length (8–128) |
| `symbols` | bool | true | Include symbols |
| `ambiguous` | bool | false | Exclude ambiguous chars (O0Il1) |

### Query Parameters for `/api/v1/credentials`

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `search` | string | — | Filter by site URL (ILIKE) |
| `cursor` | string | — | Pagination cursor |
| `limit` | int | 20 | Page size (max 100) |

---

## Security Features

- **JWT RS256** — RSA 2048-bit key pair generated on first startup
- **Argon2id** — Password hashing (memory=64MB, iterations=3, parallelism=1)
- **AES-256-GCM** — Credential encryption with unique IV per field
- **PBKDF2-HMAC-SHA256** — Key derivation (600,000 iterations, per-user salt)
- **Rate limiting** — 5 req/min per IP + 10 req/hour per username on login
- **Account lock** — 10 consecutive failures → 15 min lockout
- **HSTS + CSP** — Security headers via Spring Security
- **Async audit log** — All sensitive operations logged
- **No sensitive data in logs** — Password fields masked

---

## Technical Decisions

### Architecture

This project follows **Domain-Driven Design (DDD)** with four well-defined layers:

| Layer | Package | Responsibility |
|------|---------|----------------|
| **Domain** | `com.vault.domain` | Entities, Value Objects, pure business rules, repository interfaces |
| **Application** | `com.vault.application` | Use cases, flow orchestration, DTOs |
| **Infrastructure** | `com.vault.infrastructure` | Repository implementations (JDBC), security services, Spring configuration |
| **Interfaces** | `com.vault.interfaces` | HTTP controllers, request filters |

The dependency rule flows inward: outer layers depend on inner layers, never the opposite. Infrastructure implements interfaces defined by the domain (dependency inversion).

### Object Calisthenics

All **9 Object Calisthenics rules** are respected in the domain and application layers:

- **Value Objects** for every primitive (`UserId`, `Email`, `PasswordHash`, `EncryptedField`, `Tags`, etc.) — eliminates primitive obsession
- **No getters/setters** — objects expose behavior, not state
- **No `else`** — guard clauses and `Optional` across domain logic
- **First-class collections** — `Tags` encapsulates `List<String>` with rules
- **Small entities/use cases** — domain ≤ 100 lines, use cases ≤ 150 lines

Relaxations are documented only in infrastructure/interfaces (Spring Security DSL, RowMappers).

### Technologies and Rationale

**Zero JPA / Hibernate** — In a password vault, every query must be intentional and auditable. JPA can hide SQL and execution behavior; for security-sensitive code, you want to know exactly what runs and when. `NamedParameterJdbcTemplate` keeps control explicit without sacrificing too much productivity.

**JWT RS256 with keys generated at startup** — Asymmetric crypto avoids distributing a shared secret between instances. The private key signs and never leaves the process; any replica can validate tokens using only the public key.

**Argon2id for passwords** — A modern recommended choice for password storage. It is intentionally slow and memory-hard to make brute-force attacks expensive on GPUs and specialized hardware.

**AES-256-GCM + PBKDF2-HMAC-SHA256** — GCM authenticates ciphertext in addition to encrypting it, so tampering in the database is detected during decryption. A random 96-bit IV per field ensures the same plaintext encrypts differently every time. PBKDF2 derives an AES key from the user master password using a per-user salt and 600k iterations — the master password is never stored; only the derived key is used in memory and then discarded.

**Bucket4j + Caffeine** — In-memory rate limiting (no Redis, no database, no extra infrastructure). Caffeine handles bucket expiration automatically, keeping things simple and effective for the project scope.

**Flyway** — The database schema lives with the code. Every change is reviewed, versioned, and reproducible in dev, CI, and production.

**Embedded PostgreSQL in tests** — Integration tests should run against a real PostgreSQL engine without requiring Docker. `embedded-postgres` runs native PostgreSQL binaries inside the Java process, so tests work on developer machines and in CI with minimal setup.

### Applied SOLID Principles

- **SRP** — Each use case (`RegisterUserUseCase`, `LoginUseCase`, etc.) has a single reason to change
- **OCP** — New crypto algorithms implement `EncryptionService` without modifying existing code
- **LSP** — Immutable Value Objects; safe substitution across contexts
- **ISP** — Repository interfaces are segregated by aggregate (`UserRepository`, `CredentialRepository`)
- **DIP** — The domain defines interfaces; infrastructure implements them (never the other way around)
