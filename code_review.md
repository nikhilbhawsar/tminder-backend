# TMinder Backend — Comprehensive Code Review

**Date:** February 25, 2026  
**Reviewer:** Cascade (AI-assisted)  
**Repository:** [nikhilbhawsar/tminder-backend](https://github.com/nikhilbhawsar/tminder-backend)  
**Stack:** Java 21, Spring Boot 3.4.2, PostgreSQL 16, Flyway, Gradle (Kotlin DSL)

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture Review](#2-architecture-review)
3. [Code Quality Issues](#3-code-quality-issues)
4. [Security Concerns](#4-security-concerns)
5. [Database & Migrations Review](#5-database--migrations-review)
6. [Missing Essentials](#6-missing-essentials)
7. [Build & DevOps](#7-build--devops)
8. [Documentation Review](#8-documentation-review)
9. [Summary of Action Items](#9-summary-of-action-items)

---

## 1. Project Overview

TMinder (TV-Minder) is a backend API for tracking TV series, managing watchlists, and episode release schedules. It uses IMDb-style data (`tconst`, `nconst` identifiers). The project is in early stages with two working endpoints:

| Endpoint | Description |
|----------|-------------|
| `GET /api/v1/search?q={query}` | Search TV series by title (ILIKE) |
| `GET /api/v1/media/{id}/episodes` | Get episodes for a series |

### File Count

| Layer | Files | Purpose |
|-------|-------|---------|
| API / Controllers | 2 | REST endpoints |
| API / DTOs | 2 | Response records |
| Application / Services | 2 | Use cases |
| Domain / Repositories | 2 | Repository interfaces |
| Infrastructure / Persistence | 5 | JPA repos, Postgres adapters, in-memory stub |
| Infrastructure / Entities | 2 | JPA entities |
| Migrations (SQL) | 10 | Flyway schema migrations |
| Config | 1 | `application.properties` |
| **Total** | **~26 files** | |

---

## 2. Architecture Review

### 2.1 ✅ Good: Clean Architecture layering is present

The project has the right directory structure:
```
com.tminder/
├── api/            (Controllers, DTOs)
├── application/    (Use cases / services)
├── domain/         (Repository interfaces)
└── infrastructure/ (JPA, Postgres adapters, Entities)
```

The dependency flow `api → application → domain ← infrastructure` is correct — controllers call use cases, use cases depend on domain interfaces, and infrastructure implements them.

### 2.2 🔴 CRITICAL: Domain layer depends on API DTOs (Clean Architecture violation)

This is the single biggest issue in the codebase.

```java
// domain/repository/MediaRepository.java
import com.tminder.api.dto.MediaResponse;

public interface MediaRepository {
    List<MediaResponse> searchByTitle(String text, String titleType);
}

// domain/repository/TitleEpisodeRepository.java
import com.tminder.api.dto.EpisodeResponse;

public interface TitleEpisodeRepository {
    List<EpisodeResponse> findBySeriesId(String seriesId);
}
```

**The domain layer imports from `com.tminder.api.dto`** — this is an **inward dependency violation**. In Clean Architecture, the domain layer must have **zero** outward dependencies. It should define its own domain models (entities/value objects), and the API layer should map domain objects → DTOs.

**Fix:**
1. Create domain models: `domain/model/Media.java` and `domain/model/Episode.java`
2. Have repositories return domain models
3. Map domain → DTO in controllers or use cases
4. This also enables reuse of domain models for future features (watchlists, notifications, etc.)

### 2.3 🔴 Use cases also depend on API DTOs

```java
// application/service/GetEpisodesUseCase.java
import com.tminder.api.dto.EpisodeResponse;

public List<EpisodeResponse> execute(String seriesId) { ... }
```

Same issue — the application layer should return domain models, not API DTOs. The controller should perform the mapping.

### 2.4 🟡 No domain entities exist at all

The `domain/` package contains **only** repository interfaces. There are no domain entities, value objects, or business rules. The "domain" is just contracts that return API DTOs. This effectively makes the domain layer an empty shell.

**Fix:** Create proper domain models:
```java
// domain/model/Media.java
public record Media(String id, String title, String titleType, ...) {}

// domain/model/Episode.java
public record Episode(String id, int seasonNumber, int episodeNumber, 
                      Double averageRating, Integer numVotes) {}
```

### 2.5 🟡 `PostgresMediaRepository` and `PostgresTitleEpisodeRepository` are thin wrappers

```java
// PostgresMediaRepository.java — entire class is a pass-through
@Override
public List<MediaResponse> searchByTitle(String text, String titleType) {
    Pageable pageable = PageRequest.of(0, 10);
    String pattern = "%" + text + "%";
    return jpaMediaRepository.searchMedia(titleType, pattern, pageable).getContent();
}
```

These adapter classes exist only to bridge `JpaRepository` → domain `Repository`. This is correct for Clean Architecture, but right now they add indirection with no extra logic. Consider whether the indirection pays off yet, or if the JPA repositories could implement the domain interfaces directly until business logic grows.

### 2.6 🟡 `InMemoryMediaRepository` ignores the `titleType` parameter

```java
// InMemoryMediaRepository.java:26-30
@Override
public List<MediaResponse> searchByTitle(String text, String titleType) {
    return mediaList.stream()
            .filter(m -> m.title().toLowerCase().contains(text.toLowerCase()))
            .limit(10)
            .toList();
}
```

The `titleType` parameter is silently ignored. The in-memory implementation doesn't filter by type, so test behavior diverges from production behavior.

### 2.7 🟡 Bean conflict: `@Repository` + `@Primary` + `@Profile` pattern

```java
@Repository @Profile("test")
public class InMemoryMediaRepository implements MediaRepository { ... }

@Component @Primary
public class PostgresMediaRepository implements MediaRepository { ... }
```

`PostgresMediaRepository` uses `@Primary` and `InMemoryMediaRepository` uses `@Profile("test")`. This works, but `@Primary` means the Postgres bean wins in **all** profiles including test unless explicitly overridden. The `@Profile("test")` on the in-memory repo means it's only created in test profile, but `@Primary` on Postgres means it still takes precedence. Consider using `@Profile("!test")` on the Postgres adapter instead of `@Primary`.

---

## 3. Code Quality Issues

### 3.1 🔴 SQL injection risk in search pattern construction

```java
// PostgresMediaRepository.java:25
String pattern = "%" + text + "%";
```

While Spring Data JPA parameterizes the query, the `%` wildcards in the user input are **not escaped**. A user searching for `%` or `_` (SQL LIKE wildcards) will get unexpected results. For example, searching for `_` matches any single character.

**Fix:** Escape LIKE special characters before wrapping:
```java
String escaped = text.replace("%", "\\%").replace("_", "\\_");
String pattern = "%" + escaped + "%";
```

### 3.2 🔴 `TitleEpisodeEntity` is missing getters/setters

```java
// TitleEpisodeEntity.java:24
// getters & setters   ← comment placeholder, no actual methods
```

The class has fields but only a comment saying "getters & setters". JPA needs these for entity population. Either:
- Add actual getters/setters
- Use Lombok `@Data` / `@Getter` / `@Setter`
- Since this entity is only used via JPQL constructor expressions (`SELECT new ...`), it may work with field access, but this is fragile

### 3.3 🟡 `MediaEntity` has 149 lines of boilerplate

`MediaEntity.java` is 149 lines, mostly manual getters/setters for 13 fields. This is a textbook case for **Lombok** or **Java records** (though records aren't JPA-friendly yet).

**Recommendation:** Add Lombok dependency and use `@Getter @Setter @NoArgsConstructor`:
```groovy
compileOnly("org.projectlombok:lombok")
annotationProcessor("org.projectlombok:lombok")
```

This reduces `MediaEntity` from 149 lines to ~35.

### 3.4 🟡 `PostgreSQL` array columns lack proper JPA mapping

```java
// MediaEntity.java:27-34
@Column(name = "directors", columnDefinition = "text[]")
private List<String> directors;

@Column(name = "writers", columnDefinition = "text[]")
private List<String> writers;

@Column(name = "genres", columnDefinition = "text[]")
private List<String> genres;
```

Standard JPA doesn't support PostgreSQL `text[]` natively. This will likely fail at runtime without a custom `AttributeConverter` or Hibernate-specific type mapping (e.g., `@Type(StringArrayType.class)` from `hypersistence-utils`).

**Fix:** Either:
- Add `io.hypersistence:hypersistence-utils-hibernate-63` dependency with `@Type(StringArrayType.class)`
- Or write a custom `AttributeConverter<List<String>, String[]>`

### 3.5 🟡 Hardcoded page size

```java
// PostgresMediaRepository.java:24
Pageable pageable = PageRequest.of(0, 10);
```

Page size 10 is hardcoded. The search endpoint doesn't accept `page` or `size` parameters. This means:
- Users always get max 10 results
- No way to paginate
- No way to adjust result count

**Fix:** Accept `page` and `size` as optional `@RequestParam` in `SearchController` with sensible defaults.

### 3.6 🟡 `SearchMediaUseCase` hardcodes `"tvSeries"` type filter

```java
// SearchMediaUseCase.java:20
return mediaRepository.searchByTitle(query, "tvSeries");
```

The type is hardcoded to `"tvSeries"`. If the API should support searching movies, documentaries, etc., this needs to be parameterized. At minimum, make it a constant, not a magic string.

### 3.7 🟡 Unused import in `SearchController`

```java
// SearchController.java:12
import java.util.stream.Collectors;
```

`Collectors` is imported but never used. The `new ArrayList<>(searchMediaUseCase.execute(q))` doesn't use streams.

### 3.8 🟡 Unnecessary `new ArrayList<>()` wrapping

```java
// SearchController.java:25
return new ArrayList<>(searchMediaUseCase.execute(q));
```

`execute()` already returns a `List<MediaResponse>`. Wrapping in `new ArrayList<>()` creates an unnecessary copy. Return the list directly:
```java
return searchMediaUseCase.execute(q);
```

### 3.9 🟡 `MediaResponse` is too sparse

```java
public record MediaResponse(String id, String title) {}
```

For a search result, returning only `id` and `title` is very minimal. Consider including `startYear`, `genres`, `averageRating` so the client can display meaningful search results without a second API call.

---

## 4. Security Concerns

### 4.1 🔴 Plaintext credentials in `application.properties`

```properties
spring.datasource.username=user
spring.datasource.password=password
```

Database credentials are hardcoded in the committed properties file. Even for a learning project, this establishes bad habits.

**Fix:**
- Use environment variables: `spring.datasource.password=${DB_PASSWORD}`
- Or use `application-local.properties` (already in `.gitignore`) for local overrides
- Or use Spring profiles with a `.env` file

### 4.2 🔴 Docker Compose uses weak credentials

```yaml
POSTGRES_USER: user
POSTGRES_PASSWORD: password
```

Same issue — weak default credentials. Use `.env` file for Docker Compose:
```yaml
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

### 4.3 🟡 No authentication or authorization

The API has no auth at all. Any caller can search and access all data. For a learning project this is fine, but should be noted as a planned feature.

### 4.4 🟡 No rate limiting

No rate limiting on search endpoint. The ILIKE query with `%text%` can be expensive on large datasets.

### 4.5 🟡 No input validation on path variables

```java
// EpisodeController.java:23
public List<EpisodeResponse> getEpisodes(@PathVariable String id) {
    return getEpisodesUseCase.execute(id);
}
```

The `id` parameter is not validated. While the use case checks for blank, there's no format validation (IMDb IDs follow `tt\d+` pattern). Consider adding `@Pattern` validation.

### 4.6 🟡 `show-sql=true` should not be in default profile

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

SQL logging in the default profile will leak data to logs in production. Move to a `dev` profile.

---

## 5. Database & Migrations Review

### 5.1 ✅ Good: Flyway is properly configured

10 incremental migrations with `V{n}__` naming convention. `ddl-auto=validate` ensures Hibernate checks against the real schema. This is correct.

### 5.2 🔴 Duplicate index in V8 and V10

```sql
-- V8__create_title_episodes_table.sql:8
CREATE INDEX IF NOT EXISTS idx_episodes_parent ON title_episodes(parent_tconst);

-- V10__add_index_on_title_episodes_parent.sql:1
CREATE INDEX idx_title_episodes_parent ON title_episodes(parent_tconst);
```

V10 creates a **second** index on `title_episodes(parent_tconst)` — same column, different name. This wastes disk space and slows writes. The `IF NOT EXISTS` in V8 means V8's index was already created. V10 then creates a duplicate.

**Fix:** Remove V10 (if no production data depends on it) or create a new migration to drop the duplicate index.

### 5.3 🟡 V9 requires `pg_trgm` extension

```sql
-- V9__create_tvseries_trgm_partial_index.sql
USING GIN (title gin_trgm_ops)
```

This migration requires the `pg_trgm` PostgreSQL extension to be enabled. There's no migration that runs `CREATE EXTENSION IF NOT EXISTS pg_trgm;` before V9. This will fail on a fresh database.

**Fix:** Add a migration (e.g., `V0__enable_extensions.sql`) with:
```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;
```

### 5.4 🟡 V1 uses `VARCHAR(255)` then V3 changes to `TEXT`

V1 creates `id VARCHAR(255)` and `title VARCHAR(255)`, then V3 alters them to `TEXT`. This could have been `TEXT` from the start. Not a bug, but shows schema design was iterative rather than planned.

### 5.5 🟡 `names` and `title_principals` tables are unused

V5 creates `names` table and V6 creates `title_principals` table, but **no entities, repositories, or services reference them**. They're schema-only with no application code.

This is fine for pre-loading IMDb data, but document the intent — otherwise it looks like dead schema.

### 5.6 🟡 No foreign key from `title_episodes.parent_tconst` to `media.id` enforcement consideration

The FK constraint exists (`REFERENCES media(id)`), which is good. However, consider adding `ON DELETE CASCADE` if parent series deletion should cascade to episodes.

---

## 6. Missing Essentials

### 6.1 🔴 No tests exist

There are **zero test files** in the repository (`src/test/` directory doesn't exist or is empty). For a project claiming "enterprise-grade" in the README, this is a significant gap.

**Minimum tests needed:**
- Unit tests for `SearchMediaUseCase` and `GetEpisodesUseCase`
- Integration tests for controllers using `@WebMvcTest`
- Repository tests using `@DataJpaTest` with an embedded DB (H2 or Testcontainers)

### 6.2 🔴 No global error handling

There's no `@ControllerAdvice` or `@ExceptionHandler`. If:
- The database is down → 500 with Spring's default Whitelabel error page
- Invalid parameters → unstructured error response
- JPA throws → raw stack trace in response

**Fix:** Add a `GlobalExceptionHandler`:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) { ... }
}
```

### 6.3 🟡 No health check / actuator

No Spring Boot Actuator for health checks, metrics, or readiness probes. Add:
```groovy
implementation("org.springframework.boot:spring-boot-starter-actuator")
```

### 6.4 🟡 No API documentation (OpenAPI/Swagger)

No Springdoc or Swagger UI. For an API project, self-documenting endpoints are essential:
```groovy
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
```

### 6.5 🟡 No CORS configuration

No CORS setup. If a frontend will consume this API, CORS will block requests.

### 6.6 🟡 No logging configuration

No `logback.xml` or `logback-spring.xml`. The app uses Spring Boot defaults, which is fine for dev but needs tuning for production.

### 6.7 🟡 No application profiles

Only a single `application.properties`. There should be at least:
- `application-dev.properties` (show-sql, debug logging)
- `application-prod.properties` (no show-sql, externalized credentials)

---

## 7. Build & DevOps

### 7.1 ✅ Good: Modern stack choices

- Java 21 (latest LTS)
- Spring Boot 3.4.2 (current)
- PostgreSQL 16 (current)
- Gradle Kotlin DSL (modern)
- Docker Compose for local DB

### 7.2 🟡 No Dockerfile for the application

`docker-compose.yml` only defines PostgreSQL. There's no `Dockerfile` to containerize the Spring Boot app itself. For a complete local dev setup:

```dockerfile
FROM eclipse-temurin:21-jre-alpine
COPY build/libs/tminder-backend-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 7.3 🟡 No CI/CD pipeline

No GitHub Actions, GitLab CI, or similar. Even a basic workflow for build + test on PR would add value.

### 7.4 🟡 Bruno API collection is committed but incomplete

The `TMinder API/` directory has Bruno collection files, which is nice for manual testing. But:
- Only 2 requests (search + episodes)
- No environment variables for base URL
- Query params in URL aren't URL-encoded (`family man` has a space)

---

## 8. Documentation Review

### 8.1 ✅ Good: README is clear and well-structured

The README has a good overview, tech stack, and links to setup/db guides. The planned features checklist is helpful.

### 8.2 ✅ Good: Setup guide covers both macOS and Windows

`setup.md` is practical with Homebrew/Scoop commands and IDE setup steps.

### 8.3 🟡 `db_setup.md` references dump files but no sample data

The DB setup guide explains how to import a PostgreSQL dump, but no dump file is included in the repo (nor should it be if it's large). Consider providing a small seed script or sample SQL insert.

### 8.4 🟡 README says "enterprise-grade" — codebase doesn't match (yet)

The README describes the project as "enterprise-grade backend service" but it lacks tests, error handling, auth, logging config, and CI/CD. Adjust the language to "learning project" or add those components.

---

## 9. Summary of Action Items

### 🔴 Critical (Fix before any further development)

| # | Action | Files |
|---|--------|-------|
| 1 | **Fix Clean Architecture violation**: Domain layer must not import API DTOs. Create domain models and map in controllers. | `MediaRepository`, `TitleEpisodeRepository`, both use cases, both controllers |
| 2 | **Add global error handling** (`@ControllerAdvice`) | New: `GlobalExceptionHandler.java` |
| 3 | **Add pg_trgm extension migration** before V9 | New: `V0__enable_extensions.sql` |
| 4 | **Remove duplicate index** (V8 vs V10 on `parent_tconst`) | `V10__add_index_on_title_episodes_parent.sql` |
| 5 | **Externalize credentials** from `application.properties` | `application.properties`, `docker-compose.yml` |
| 6 | **Add tests** — at minimum unit tests for use cases | New: `src/test/` |
| 7 | **Fix `TitleEpisodeEntity`** — add actual getters/setters | `TitleEpisodeEntity.java` |
| 8 | **Fix PostgreSQL array mapping** for `List<String>` fields | `MediaEntity.java` |

### 🟡 Medium Priority (Before first release)

| # | Action | Notes |
|---|--------|-------|
| 9 | Escape LIKE wildcards in search pattern | `PostgresMediaRepository.java` |
| 10 | Add pagination params to search endpoint | `SearchController`, `SearchMediaUseCase` |
| 11 | Enrich `MediaResponse` with more fields | `MediaResponse.java` |
| 12 | Remove unused import (`Collectors`) | `SearchController.java` |
| 13 | Remove unnecessary `new ArrayList<>()` | `SearchController.java` |
| 14 | Add `@Profile("!test")` instead of `@Primary` | `PostgresMediaRepository.java` |
| 15 | Make `InMemoryMediaRepository` respect `titleType` | `InMemoryMediaRepository.java` |
| 16 | Move `show-sql` to dev profile only | `application.properties` |
| 17 | Add Spring Actuator for health checks | `build.gradle.kts` |
| 18 | Add Springdoc OpenAPI for API docs | `build.gradle.kts` |
| 19 | Parameterize `"tvSeries"` magic string | `SearchMediaUseCase.java` |
| 20 | Add input validation on `@PathVariable id` | `EpisodeController.java` |

### 🔵 Low Priority (Nice-to-have)

| # | Action |
|---|--------|
| 21 | Add Lombok to reduce entity boilerplate |
| 22 | Add `Dockerfile` for the application |
| 23 | Add GitHub Actions CI pipeline |
| 24 | Add CORS configuration |
| 25 | Add logging configuration (`logback-spring.xml`) |
| 26 | Add application profiles (`dev`, `prod`) |
| 27 | Document `names` and `title_principals` tables usage intent |
| 28 | Update README language from "enterprise-grade" to match current state |
| 29 | URL-encode Bruno API collection params |

---

## Overall Assessment

**Rating: 6/10** — Good foundation with correct architectural intent, but significant gaps.

**Strengths:**
- Clean Architecture layering is correctly intended
- Modern tech stack (Java 21, Spring Boot 3.4, PostgreSQL 16)
- Flyway migrations are well-organized
- Good documentation (README, setup guides)
- Constructor injection throughout (no field injection)
- Proper use of Spring profiles for test vs production repos
- Java records for DTOs (idiomatic, immutable)

**Key Weaknesses:**
- Domain layer violates Clean Architecture by depending on API DTOs
- Zero tests
- No error handling infrastructure
- Hardcoded credentials committed to repo
- Missing getters/setters in `TitleEpisodeEntity`
- PostgreSQL array type mapping will likely fail at runtime
- Duplicate database index

The architecture is on the right track — the layering and separation of concerns show understanding of Clean Architecture principles. The main issue is that the domain layer is hollow (no real domain models) and improperly coupled to the API layer. Fixing item #1 (domain models) will unlock clean growth for all planned features.

---

*End of review.*
