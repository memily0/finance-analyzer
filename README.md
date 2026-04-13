# personal-finance-analyzer

Backend-сервис на Kotlin и Spring Boot для хранения пользовательских транзакций и анализа личных финансов: summary-метрики, аналитика по категориям и месяцам, rule-based insights, поиск регулярных подписок и автоопределение категорий по описанию.

## Stack

- Kotlin 2.3
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- springdoc OpenAPI / Swagger UI
- JUnit 5
- MockK + springmockk
- H2 для тестов

## Архитектура

- `controller` — REST endpoints
- `service` — бизнес-логика
- `repository` — доступ к данным
- `entity` — JPA-сущности
- `dto` — request/response модели
- `mapper` — преобразование entity <-> dto
- `exception` — единая обработка ошибок API

Сущности:
- `User`: `id`, `email`
- `Transaction`: `id`, `user`, `amount`, `type`, `category`, `description`, `timestamp`

## API

### Transactions

- `POST /api/transactions` — создать одну транзакцию
- `POST /api/transactions/bulk` — создать несколько транзакций
- `GET /api/transactions?userId={id}` — получить транзакции пользователя
- `GET /api/transactions/{id}` — получить транзакцию по id

### Analytics

- `GET /api/analytics/summary?userId={id}` — summary-аналитика
- `GET /api/analytics/categories?userId={id}` — аналитика расходов по категориям
- `GET /api/analytics/monthly?userId={id}` — аналитика по месяцам
- `GET /api/analytics/insights?userId={id}` — rule-based инсайты
- `GET /api/analytics/subscriptions?userId={id}` — поиск регулярных списаний

Swagger UI:
- `/swagger-ui.html`

OpenAPI JSON:
- `/api-docs`

## Запуск локально

1. Поднять PostgreSQL:

```bash
docker compose up -d postgres
```

2. Запустить приложение:

```bash
./gradlew bootRun
```

По умолчанию используются:
- DB `finance_analyzer`
- user `finance_user`
- password `finance_password`

## Запуск через Docker Compose

```bash
docker compose up --build
```

## Тесты

```bash
./gradlew test
```

## Примеры запросов

Создание транзакции:

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 120.50,
    "type": "EXPENSE",
    "description": "Uber trip",
    "timestamp": "2026-04-10T19:30:00"
  }'
```

Пример ответа:

```json
{
  "id": 10,
  "userId": 1,
  "amount": 120.50,
  "type": "EXPENSE",
  "category": "TRANSPORT",
  "description": "Uber trip",
  "timestamp": "2026-04-10T19:30:00"
}
```

Summary analytics:

```bash
curl "http://localhost:8080/api/analytics/summary?userId=1"
```

## Как это работает

- при создании транзакции сервис проверяет пользователя
- категория может быть определена автоматически по `description`
- аналитика считает доходы, расходы, баланс, средний и максимальный расход
- insights строятся по простым rule-based эвристикам
- subscriptions ищутся как повторяющиеся списания с похожим описанием, близкой суммой и интервалом около месяца

## Future improvements

- аутентификация и multi-tenant security
- пагинация и фильтрация транзакций
- импорт CSV/банковских выписок
- более гибкие правила категоризации
- richer anomaly detection
